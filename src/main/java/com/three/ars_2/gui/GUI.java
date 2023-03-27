//////
////
//// Run gui for simulation
////
//// This file was written by Cavid Karca
////
//////

package com.three.ars_2.gui;

import com.three.ars_2.simulation.Controller;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUI extends Application {

    public static void main(String[] args) {
        launch();
    }
    private MainScene scene;
    private StackPane pane;
    private Controller controller;
    public GUI(){
        pane = new StackPane();
        controller = new Controller();
        scene = new MainScene(pane,controller);
        controller.setMainScene(scene);
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("Robot Simulation");
        stage.setScene(scene);
        Thread thread = new Thread(controller);
        thread.start();
        stage.show();
    }
}
