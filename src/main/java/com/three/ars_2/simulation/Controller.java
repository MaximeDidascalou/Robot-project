package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    private final double TIME_STEP = 1.0/30;
    private final double SPEED_INCREMENT = 0.1;
    private final World WORLD = new World(6,6);
    private MainScene mainScene;

    private void incrementRobotParameters(Robot robot, double incrementLeft, double incrementRight){
        robot.setWheelSpeeds( robot.getWheelSpeeds()[0] + incrementLeft, robot.getWheelSpeeds()[1] + incrementRight);
    }

    public void updateRobotParameters(Robot robot, int keyPress){
        switch (keyPress) {
            case 87 -> incrementRobotParameters(robot, 0.0, SPEED_INCREMENT); // W
            case 83 -> incrementRobotParameters(robot, 0.0, -SPEED_INCREMENT); // S
            case 79 -> incrementRobotParameters(robot, SPEED_INCREMENT, 0.0); // O
            case 76 -> incrementRobotParameters(robot, -SPEED_INCREMENT, 0.0); // L
            case 84 -> incrementRobotParameters(robot, SPEED_INCREMENT, SPEED_INCREMENT); // T
            case 71 -> incrementRobotParameters(robot, -SPEED_INCREMENT, -SPEED_INCREMENT); // G
            case 88 -> robot.setWheelSpeeds(0, 0); // X
        }
    }

    public void runSimulation(double timeStep) {
        for(Robot robot : WORLD.getRobots()){
            robot.update(timeStep);
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
}
