package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    private final double TIME_STEP = 1.0/30;
    private final double SPEED_INCREMENT = 0.1;
    private final World WORLD = new World();
    private MainScene mainScene;

    private void incrementRobotParameters(Robot robot, double incrementLeft, double incrementRight){
        robot.setVW( robot.getVW()[0] + incrementLeft, robot.getVW()[1] + incrementRight);
    }

    public void updateRobotParameters(Robot robot, int keyPress){
        switch (keyPress) {
            case 87 -> incrementRobotParameters(robot, SPEED_INCREMENT, 0.0); // W
            case 83 -> incrementRobotParameters(robot, -SPEED_INCREMENT, 0.0); // S
            case 79 -> incrementRobotParameters(robot, 0.0, SPEED_INCREMENT); // O
            case 76 -> incrementRobotParameters(robot, 0.0, -SPEED_INCREMENT); // L
            case 88 -> robot.setVW(0, 0); // X
        }
    }

    public void runSimulation(double timeStep) {
        WORLD.setTimeStep(1.0/30);
        WORLD.initialiseRobots();
        for(Robot robot : WORLD.getRobots()){
            robot.updateKalman();
        }
    }

    public void run() {
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
