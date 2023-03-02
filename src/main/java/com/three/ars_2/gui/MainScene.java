package com.three.ars_2.gui;

import com.three.ars_2.simulation.Controller;
import com.three.ars_2.simulation.Robot;
import com.three.ars_2.simulation.World;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.util.ArrayList;

public class MainScene extends Scene {
    private static double border = 100;
    private StackPane mainStackPane;
    private VBox controlVBox;
    private World world;
    private Controller controller;
    private Canvas canvasBackground;
    private Canvas canvasMovables;
    private double robotVel_l = 0;
    private double robotVel_r = 0;
    private ArrayList<ControlDisplay> displays = new ArrayList<>();

    public MainScene(StackPane mainStackPane, Controller controller) {
        super(mainStackPane,controller.getWorld().getWidth()*GuiSettings.SCALING +border*2,controller.getWorld().getHeight()*GuiSettings.SCALING +border*2);
        double width = controller.getWorld().getWidth()*GuiSettings.SCALING;
        double height = controller.getWorld().getHeight()*GuiSettings.SCALING;
        this.world = controller.getWorld();
        this.controller = controller;
        canvasBackground = new Canvas(width,height);
        canvasMovables = new Canvas(width,height);
        this.mainStackPane = mainStackPane;
        controlVBox = new VBox();
        controlVBox.setSpacing(10);
        this.mainStackPane.setMinSize(width+border*2,height+border*2);
        File file = new File("src/main/java/com/three/ars_2/gui/stylesheet.css");
        this.getStylesheets().add(file.toURI().toString());
        mainStackPane.getChildren().add(canvasBackground);
        mainStackPane.getChildren().add(canvasMovables);
        mainStackPane.getChildren().add(controlVBox);
        drawBackground();
        drawMovables();
        addListeners();
        createControlDisplays();
        drawControlDisplays();
    }
    public void drawBackground(){
        GraphicsContext g = canvasBackground.getGraphicsContext2D();
        world.draw(g);
    }
    public void drawMovables(){
        GraphicsContext g = canvasMovables.getGraphicsContext2D();
        g.clearRect(0,0,world.getWidth()*GuiSettings.SCALING,world.getHeight()*GuiSettings.SCALING);
        for (Robot r:world.getRobots()){
            r.draw(g);
        }
    }

    public void addListeners(){
        this.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            double incr = GuiSettings.controlIncrement;
            switch (keyEvent.getCode().getCode()) {
                case 87 -> updateRobot(0.0, incr); // W
                case 83 -> updateRobot(0.0, -incr); // S
                case 79 -> updateRobot(incr, 0.0); // O
                case 76 -> updateRobot(-incr, 0.0); // L
                case 84 -> updateRobot(incr, incr); // T
                case 71 -> updateRobot(-incr, -incr); // G
                case 88 -> {
                    robotVel_r = 0;
                    robotVel_l = 0;
                    controller.setRobotParameters(robotVel_r, robotVel_l);
                    drawControlDisplays();
                }// X
            }
        });
    }
    public void createControlDisplays(){
        for (Robot r:world.getRobots()){
            ControlDisplay c = new ControlDisplay(r);
            displays.add(c);
            controlVBox.getChildren().add(c);

        }
    }
    public void drawControlDisplays(){
        for(ControlDisplay c: displays){
            c.redraw();
        }
    }
    private void updateRobot(double changeLeft,double changeRight){
        robotVel_r = robotVel_r+ changeLeft;
        robotVel_l = robotVel_l+ changeRight;
        controller.setRobotParameters(robotVel_r,robotVel_l);
        drawControlDisplays();
    }
}
