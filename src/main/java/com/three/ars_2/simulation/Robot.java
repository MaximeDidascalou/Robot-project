package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import com.three.ars_2.gui.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Robot {
    private final World WORLD; //list of all the walls in the world
    final int NUM_SENSORS; //number of sensors
    private final double MAX_SENSOR_DISTANCE; //Maximum distance a sensor can see
    private final double[] SENSOR_ANGLES; //Sensor angles, relative to bot angle
    double[] sensorValues;
    private final double DIAMETER; //diameter
    private final double WHEEL_DISTANCE; //length between wheels
    private final double MAX_WHEEL_SPEED;
    private double[] position;
    private double[] wheelSpeeds;
    private double angle;
    private final String NAME;

    Robot(World world, int numSensors, double maxSensorDistance, double diameter, double wheelDistance, double maxWheelSpeed, String name, double[] position, double[] wheelSpeeds, double angle){
        this.WORLD = world;
        this.NUM_SENSORS = numSensors;
        this.MAX_SENSOR_DISTANCE = maxSensorDistance;
        SENSOR_ANGLES = new double[numSensors];
        sensorValues = new double[numSensors];
        createSensors();
        this.DIAMETER = diameter;
        this.WHEEL_DISTANCE = wheelDistance;
        this.MAX_WHEEL_SPEED = maxWheelSpeed;
        this.NAME = name;

        this.position = position;
        this.wheelSpeeds = wheelSpeeds;
        this.angle = angle;
    }

    Robot(World world, double[] position, double angle, String name) {
        this(world, 12, 5, .5, .4, 1.0, name, position, new double[]{0.0, 0.0}, angle);
    }

    public void createSensors(){
        double angleInterval = 2*Math.PI/ NUM_SENSORS;

        for(int i = 0; i < NUM_SENSORS; i++) {
            SENSOR_ANGLES[i] = angle + i*angleInterval;
        }
    }

    public void updateSensorValues() {
        // update sensor values
        for (int i = 0; i < NUM_SENSORS; i++) {
            sensorValues[i] = calculateSensorValue(angle + SENSOR_ANGLES[i]);
        }
    }

    public double calculateSensorValue(double sensorAngle) {
        double sensorX = position[0] + (MAX_SENSOR_DISTANCE + DIAMETER/2) * Math.cos(sensorAngle);
        double sensorY = position[1] + (MAX_SENSOR_DISTANCE + DIAMETER/2) * Math.sin(sensorAngle);
        double minimumSquared = Math.pow(MAX_SENSOR_DISTANCE + DIAMETER/2, 2);
        for (double[] wall : WORLD.getEnvironment()) {
            double[] intersect = lineIntersect(position[0], position[1], sensorX, sensorY, wall[0], wall[1], wall[2], wall[3]);
            if (intersect != null) {
                double distanceSquared = Math.pow((intersect[1] - position[1]), 2) + Math.pow((intersect[0] - position[0]), 2);
                if (distanceSquared < minimumSquared) {
                    minimumSquared = distanceSquared;
                }
            }
        }
        return Math.sqrt(minimumSquared) - DIAMETER/2;
    }

    public void update(double timeStep){
        double[] newPosition = new double[2];

        if (Math.abs(wheelSpeeds[0] - wheelSpeeds[1]) < 0.000001) {
            newPosition[0] = position[0] + Math.cos(angle) * wheelSpeeds[0] * timeStep;
            newPosition[1] = position[1] + Math.sin(angle) * wheelSpeeds[0] * timeStep;
        } else {
            double radius = (WHEEL_DISTANCE / 2) * ((wheelSpeeds[0] + wheelSpeeds[1]) / (wheelSpeeds[1] - wheelSpeeds[0]));
            double omega = (wheelSpeeds[1] - wheelSpeeds[0]) / WHEEL_DISTANCE;

            double[] ICC = {position[0] - radius * Math.sin(angle), position[1] + radius * Math.cos(angle)};
            newPosition[0] = Math.cos(omega*timeStep)*(position[0] - ICC[0]) - Math.sin(omega*timeStep)*(position[1] - ICC[1]) + ICC[0];
            newPosition[1] = Math.sin(omega*timeStep)*(position[0] - ICC[0]) + Math.cos(omega*timeStep)*(position[1] - ICC[1]) + ICC[1];

            angle = angle + omega * timeStep;
        }

        collisionCheck(newPosition);
        updateSensorValues();
    }

    private static double[] lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        // Lines are parallel.
        if (denom == 0.0) return null;

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            // Get the intersection point.
            return new double[]{(x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1))};
        }

        return null;
    }

    private void collisionCheck(double[] newPosition){
        for (double[] wall : WORLD.getEnvironment()) {
            double[] intersect = closestPointOnLine(wall[0], wall[1], wall[2], wall[3], newPosition[0], newPosition[1]);
            double distanceSquared = Math.pow((intersect[0] - newPosition[0]), 2) + Math.pow((intersect[1] - newPosition[1]), 2);
            if (distanceSquared < Math.pow(DIAMETER/2, 2)) {
                double distance = Math.sqrt(distanceSquared);
                int jumpedWall = lineIntersect(position[0], position[1], newPosition[0], newPosition[1], wall[0], wall[1], wall[2], wall[3]) != null ? -1 : 1;

                newPosition[0] = intersect[0] + (newPosition[0] - intersect[0]) / distance * DIAMETER/2 * jumpedWall;
                newPosition[1] = intersect[1] + (newPosition[1] - intersect[1]) / distance * DIAMETER/2 * jumpedWall;
            }
        }

        position = newPosition;
    }

    private static double[] closestPointOnLine(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        double A = pointX - x1;
        double B = pointY - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        return new double[]{xx, yy};
    }

    public String getName() {
        return NAME;
    }

    public double[] getPosition() {
        return position;
    }

    public double[] getWheelSpeeds() {
        return wheelSpeeds;
    }

    public void setWheelSpeeds(double[] wheelSpeeds) {
        this.wheelSpeeds = wheelSpeeds;
    }

    public void setWheelSpeeds(double leftSpeed, double rightSpeed) {
        wheelSpeeds[0] = clamp(leftSpeed, -MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        wheelSpeeds[1] = clamp(rightSpeed, -MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        System.out.println(Arrays.toString(wheelSpeeds));
    }

    private static double clamp(double input, double min, double max){
        return Math.min(Math.max(input, min), max);
    }

    public double getAngle() {
        return angle;
    }

    public void draw(GraphicsContext g){
        double radius = DIAMETER / 2 * GuiSettings.SCALING;
        double x = position[0] * GuiSettings.SCALING;
        double y = position[1] * GuiSettings.SCALING;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        //draw sensor lines
        for (int i = 0; i < NUM_SENSORS; i++) {
            Vector2D v2 = new Vector2D(angle + SENSOR_ANGLES[i]);
            v2.multiply((sensorValues[i] + DIAMETER/2) * GuiSettings.SCALING);
            g.setStroke(GuiSettings.SENSOR_COLOR);
            g.setLineWidth(1.0);
            g.strokeLine(x, y, x + v2.x, y + v2.y);
        }
        //draw sensor values
        for(int i = 0; i < NUM_SENSORS; i++) {
            Vector2D v2 = new Vector2D(angle + SENSOR_ANGLES[i]);
            v2.multiply(DIAMETER * GuiSettings.SCALING);
            g.strokeLine(x,y,x + v2.x,y + v2.y);
            g.strokeText(df.format(sensorValues[i]),x+v2.x,y+v2.y);
        }
        //draw body
        g.setFill(GuiSettings.ROBOT_COLOR);
        g.fillOval(x-radius,y-radius,radius*2,radius*2);
        g.setLineWidth(1);
        //draw head
        Vector2D v = new Vector2D(angle);
        v.multiply(radius*1);
//        g.setFill(GuiSettings.DIRECTION_COLOR);
//        g.fillOval(x+v.x-radius/2,y+v.y-radius/2,2*radius/2,2*radius/2);
        g.setStroke(GuiSettings.DIRECTION_COLOR);
        g.setLineWidth(2.0);
        g.strokeLine(x, y, x + v.x, y + v.y);
    }
}