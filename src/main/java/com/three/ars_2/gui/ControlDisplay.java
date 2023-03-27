//////
////
//// idk
////
//// This file was written by Cavid Karca
////
//////

package com.three.ars_2.gui;

import com.three.ars_2.simulation.Robot;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;

public class ControlDisplay extends VBox {
    Robot robot;
    Label angleLabel = new Label("Angle:");
    Label leftLabel = new Label("Velocity Left:");
    Label rightLabel = new Label("Velocity Right:");
    Label dustLabel = new Label("Dust collected:");
    Label collisionLabel = new Label("Ticks in wall:");
    Label name;
    DecimalFormat df = new DecimalFormat();
    public ControlDisplay(Robot robot){
        this.robot = robot;
        this.name = new Label(robot.getName());
        this.getChildren().addAll(name,leftLabel, rightLabel, angleLabel, dustLabel, collisionLabel);
        name.setText(robot.getName());
        name.getStyleClass().add("text-bold");
        df.setMaximumFractionDigits(1);
        redraw();
    }
    public void redraw(){
        leftLabel.setText("Velocity Left: "+ df.format(robot.getWheelSpeeds()[0]));
        rightLabel.setText("Velocity Right: "+ df.format(robot.getWheelSpeeds()[1]));
        angleLabel.setText("Angle: "+ df.format(((robot.getAngle()/Math.PI*180)%360+180)%360-180));
        dustLabel.setText("Dust collected: " + robot.getTotalDustCollected());
        collisionLabel.setText("Ticks in wall: " + robot.getTicksInWall());
    }
}
