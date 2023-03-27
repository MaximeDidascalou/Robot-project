//////
////
//// idk
////
//// This file was written by Cavid Karca
////
//////

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
    private static double border = 128;
    private StackPane mainStackPane;
    private VBox controlVBox;
    private final World WORLD;
    private Controller controller;
    private Canvas canvasBackground;
    private Canvas canvasMovables;
    private ArrayList<ControlDisplay> displays = new ArrayList<>();

    public MainScene(StackPane mainStackPane, Controller controller) {
        super(mainStackPane,controller.getWorld().getWidth()*GuiSettings.SCALING +border*2,controller.getWorld().getHeight()*GuiSettings.SCALING +border*2);
        double width = controller.getWorld().getWidth()*GuiSettings.SCALING;
        double height = controller.getWorld().getHeight()*GuiSettings.SCALING;
        this.WORLD = controller.getWorld();
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
        WORLD.draw(g);
    }
    public void drawMovables(){
        GraphicsContext g = canvasMovables.getGraphicsContext2D();
        g.clearRect(0,0, WORLD.getWidth()*GuiSettings.SCALING, WORLD.getHeight()*GuiSettings.SCALING);
        for (Robot robot : WORLD.getRobots()){
            robot.draw(g);
        }
    }
    public void addListeners(){
        this.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> controller.keyPressed(keyEvent.getCode().getCode()));
    }
    public void createControlDisplays() {
        for (Robot robot : WORLD.getRobots()) {
            ControlDisplay c = new ControlDisplay(robot);
            displays.add(c);
            controlVBox.getChildren().add(c);
        }
    }
    public void drawControlDisplays(){
        for(ControlDisplay c: displays){
            c.redraw();
        }
    }
}
