package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    private final double TIME_STEP = 1.0/30;
    private final double SPEED_INCREMENT = 0.02;
    private final double OMEGA_INCREMENT = 0.1;
    private final World WORLD = new World();
    private MainScene mainScene;

    private void incrementRobotParameters(Robot robot, double incrementLeft, double incrementRight){
        robot.setVW(robot.getVW()[0] + incrementLeft, robot.getVW()[1] + incrementRight);
    }

    public void keyPressed(int keyPress){
        switch (keyPress) {
            case 87 -> { // W
                for(Robot robot: WORLD.getRobots()){
                    incrementRobotParameters(robot, SPEED_INCREMENT, 0.0);
                }
            }
            case 83 -> { // S
                for(Robot robot: WORLD.getRobots()){
                    incrementRobotParameters(robot, -SPEED_INCREMENT, 0.0);
                }
            }
            case 79 -> { // O
                for(Robot robot: WORLD.getRobots()){
                    incrementRobotParameters(robot, 0.0, OMEGA_INCREMENT);
                }
            }
            case 76 -> { // L
                for(Robot robot: WORLD.getRobots()){
                    incrementRobotParameters(robot, 0.0, -OMEGA_INCREMENT);
                }
            }
            case 88 -> { // X
                for(Robot robot: WORLD.getRobots()){
                    robot.setVW(0, 0);
                }
            }
        }
    }

    public void runSimulation(double timeStep) {
        for(Robot robot : WORLD.getRobots()){
            robot.updateKalman();
        }
    }

    public void run() {
        WORLD.setTimeStep(TIME_STEP);
        WORLD.initialiseRobots();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis((int)(1000* TIME_STEP)), event -> {
            runSimulation(TIME_STEP);
            mainScene.drawMovables();
            mainScene.drawControlDisplays();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void setMainScene(MainScene mainScene) {
        this.mainScene = mainScene;
    }

    public World getWorld() {
        return WORLD;
    }
}
