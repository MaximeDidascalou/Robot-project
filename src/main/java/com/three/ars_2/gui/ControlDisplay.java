package com.three.ars_2.gui;

import com.three.ars_2.simulation.Robot;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ControlDisplay extends VBox {
    Label angleLabel = new Label("Angle:");
    Label vel_right = new Label("vel_right:");
    Label vel_left = new Label("vel_left:");
    Label name = new Label("");
    Robot robot;
    public ControlDisplay(Robot robot){
        this.robot = robot;
        this.getChildren().addAll(name,angleLabel,vel_right,vel_left);
        name.setText(robot.getName());
        name.getStyleClass().add("text-bold");
        redraw();
    }
    public void redraw(){
        vel_left.setText("vel_left: "+ robot.getVelocityLeft());
        vel_right.setText("vel_right: "+ robot.getVelocityRight());
        angleLabel.setText("Angle: "+ robot.getDirection());
    }
}
