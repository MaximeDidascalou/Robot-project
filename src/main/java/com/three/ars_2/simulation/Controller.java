package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    World world = new World(50,50);
    MainScene mainScene;
    public void setRobotParameters(double vel_r, double vel_l) {
        for(Robot r: getWorld().getRobots()){
            r.velLeft = vel_l;
            r.velRight = vel_r;
        }
    }

    public void runSimulation(double dt) {
        for(Robot r: getWorld().getRobots()){
            r.updatePosition(dt);
        }
    }

    public World getWorld() {
        return world;
    }

    public void setMainScene(MainScene mainScene) {
        this.mainScene = mainScene;
    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            runSimulation(0.1);
            mainScene.drawMovables();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
