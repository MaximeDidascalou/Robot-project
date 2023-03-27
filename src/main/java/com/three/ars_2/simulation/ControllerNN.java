//////
////
//// For running evolutionary algorithm and visualising best results, (leftv, rightv) framework
//// Need to change name to Controller to use this 
////
//// This file was written by Marco Rietjens
////
//////

package com.three.ars_2.simulation;

import com.three.ars_2.gui.MainScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller implements Runnable {
    private final double SPEED_INCREMENT = 0.1;
    private final World WORLD = new World();
    private Robot[] seedRobots;
    private Timeline timeline;
    private MainScene mainScene;

    // increment left vel and right vel
    private void incrementRobotParameters(Robot robot, double incrementLeft, double incrementRight){
        robot.setWheelSpeeds( robot.getWheelSpeeds()[0] + incrementLeft, robot.getWheelSpeeds()[1] + incrementRight);
    }

    public void keyPressed(int keyPress){
        switch (keyPress) {
//            case 87 -> incrementRobotParameters(robot, SPEED_INCREMENT, 0.0); // W
//            case 83 -> incrementRobotParameters(robot, -SPEED_INCREMENT, 0.0); // S
//            case 79 -> incrementRobotParameters(robot, 0.0, SPEED_INCREMENT); // O
//            case 76 -> incrementRobotParameters(robot, 0.0, -SPEED_INCREMENT); // L
//            case 84 -> incrementRobotParameters(robot, SPEED_INCREMENT, SPEED_INCREMENT); // T
//            case 71 -> incrementRobotParameters(robot, -SPEED_INCREMENT, -SPEED_INCREMENT); // G
//            case 88 -> robot.setWheelSpeeds(0, 0); // X
            case 32 -> {
                for(Robot robot: WORLD.getRobots()){
                    robot.initialise(); // Space, reset robots
                }
            }
            case 10 -> {
                timeline.pause();

                WORLD.setTimeStep(1.0/4);
                WORLD.runEvolution(seedRobots);
                seedRobots = WORLD.getRobots();
                WORLD.createNewGeneration(null, null, 1, 1);
                for (Robot robot: WORLD.getRobots()){
                    robot.initialise();
                }
                WORLD.setTimeStep(1.0/30);

                timeline.play();
            }
        }
    }

    // runs evolutionary algorithm and visualises results 
    public void run() {
        WORLD.setTimeStep(1.0/4);
        WORLD.initialiseRobots();
        WORLD.runSimulation();
        seedRobots = WORLD.getRobots();
        WORLD.createNewGeneration(null, null, 1, 1);
        for (Robot robot: WORLD.getRobots()){
            robot.initialise();
        }
        WORLD.setTimeStep(1.0/30);

        timeline = new Timeline(new KeyFrame(Duration.millis((int)(1000* WORLD.getTimeStep())), event -> {
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
