package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    private final double timeStep = 1.0/30;
    private final World WORLD = new World(10,10);
    private MainScene mainScene;
    public void setRobotParameters(double velocityLeft, double velocityRight) {
        for(Robot robot : getWorld().getRobots()){
            robot.velocityLeft = velocityLeft;
            robot.velocityRight = velocityRight;
        }
    }

    public void runSimulation(double dt) {
        for(Robot r: getWorld().getRobots()){
            r.updatePosition(dt);
        }
    }

    public World getWorld() {
        return WORLD;
    }

    public void setMainScene(MainScene mainScene) {
        this.mainScene = mainScene;
    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis((int)(1000*timeStep)), event -> {
            runSimulation(timeStep);
            mainScene.drawMovables();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
