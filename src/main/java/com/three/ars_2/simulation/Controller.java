package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    private final double SPEED_INCREMENT = 0.1;
    private final World WORLD = new World();
    private MainScene mainScene;

    private void incrementRobotParameters(Robot robot, double incrementLeft, double incrementRight){
        robot.setWheelSpeeds( robot.getWheelSpeeds()[0] + incrementLeft, robot.getWheelSpeeds()[1] + incrementRight);
    }

    public void updateRobotParameters(Robot robot, int keyPress){
        switch (keyPress) {
            case 87 -> incrementRobotParameters(robot, SPEED_INCREMENT, 0.0); // W
            case 83 -> incrementRobotParameters(robot, -SPEED_INCREMENT, 0.0); // S
            case 79 -> incrementRobotParameters(robot, 0.0, SPEED_INCREMENT); // O
            case 76 -> incrementRobotParameters(robot, 0.0, -SPEED_INCREMENT); // L
            case 84 -> incrementRobotParameters(robot, SPEED_INCREMENT, SPEED_INCREMENT); // T
            case 71 -> incrementRobotParameters(robot, -SPEED_INCREMENT, -SPEED_INCREMENT); // G
            case 88 -> robot.setWheelSpeeds(0, 0); // X
        }
    }

    public void run() {
        WORLD.runEvolution();
        WORLD.setTimeStep(1.0/60);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis((int)(1000* WORLD.getTimeStep())), event -> {
            WORLD.updateRobots();
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
