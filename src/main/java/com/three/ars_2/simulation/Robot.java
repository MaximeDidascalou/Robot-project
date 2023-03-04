package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import com.three.ars_2.gui.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Robot implements Comparable<Robot>{
    private final World WORLD; //reference to the world it is in
    private final String NAME;
    private final NeuralNet NEURAL_NET;
    private static final int NUM_SENSORS = 12;; //number of sensors
    private static final double MAX_SENSOR_DISTANCE = 2.0; //Maximum distance a sensor can see
    private final double[] SENSOR_ANGLES; //Sensor angles, relative to bot angle
    private final double DIAMETER; //diameter
    private final double WHEEL_DISTANCE; //length between wheels
    private final double MAX_WHEEL_SPEED;
    private final boolean DO_ACCELERATION;
    private final double MAX_ACCELERATION;
    private final double DUST_RESOLUTION;


    private double[] position;
    private double angle;
    private double[] wheelSpeeds;
    double[] sensorValues;
    private boolean[][] dustIsCollected;
    private int totalDustCollected;
    private int ticksInWall, totalTicks;


    Robot(World world, String name, NeuralNet neuralNet){
        this.WORLD = world;
        this.NAME = name;
        this.NEURAL_NET = neuralNet;
        this.SENSOR_ANGLES = new double[NUM_SENSORS];
        this.sensorValues = new double[NUM_SENSORS];
        this.createSensors();
        this.DIAMETER = 0.5;
        this.WHEEL_DISTANCE = 0.4;
        this.MAX_WHEEL_SPEED = 1.0;
        this.DO_ACCELERATION = false;
        this.MAX_ACCELERATION = 0.5;
        this.DUST_RESOLUTION = 1.0/4;
        this.dustIsCollected = new boolean[(int)(WORLD.getWidth()/DUST_RESOLUTION)][(int)(WORLD.getHeight()/DUST_RESOLUTION)];

        this.position = new double[2];
        this.reset();
    }

    //Construct robot with random NN
    Robot(World world, String name) {
        this(world, name, new NeuralNet(new int[]{12, 20, 2}, false));
    }

    private void createSensors(){
        double angleInterval = 2*Math.PI/ NUM_SENSORS;

        for(int i = 0; i < NUM_SENSORS; i++) {
            SENSOR_ANGLES[i] = angle + i*angleInterval;
        }
    }

    public void update(){
        updateWheelSpeeds();
        updatePosition();
        updateDustCollection();
        updateSensorValues();
        totalTicks++;
    }

    public void updateSensorValues() {
        // update sensor values
        for (int i = 0; i < NUM_SENSORS; i++) {
            sensorValues[i] = calculateSensorValue(angle + SENSOR_ANGLES[i]);
        }
    }

    private double calculateSensorValue(double sensorAngle) {
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

    public void updateWheelSpeeds(){
        double[] newWheelSpeeds = NEURAL_NET.evaluate(sensorValues);
        setWheelSpeeds(newWheelSpeeds[0], newWheelSpeeds[1]);
    }

    public void updatePosition(){
        double[] newPosition = new double[2];

        if (Math.abs(wheelSpeeds[0] - wheelSpeeds[1]) < 0.000001) {
            newPosition[0] = position[0] + Math.cos(angle) * wheelSpeeds[0] * WORLD.getTimeStep();
            newPosition[1] = position[1] + Math.sin(angle) * wheelSpeeds[0] * WORLD.getTimeStep();
        } else {
            double radius = (WHEEL_DISTANCE / 2) * ((wheelSpeeds[0] + wheelSpeeds[1]) / (wheelSpeeds[1] - wheelSpeeds[0]));
            double omega = (wheelSpeeds[1] - wheelSpeeds[0]) / WHEEL_DISTANCE;

            double[] ICC = {position[0] - radius * Math.sin(angle), position[1] + radius * Math.cos(angle)};
            newPosition[0] = Math.cos(omega*WORLD.getTimeStep())*(position[0] - ICC[0]) - Math.sin(omega*WORLD.getTimeStep())*(position[1] - ICC[1]) + ICC[0];
            newPosition[1] = Math.sin(omega*WORLD.getTimeStep())*(position[0] - ICC[0]) + Math.cos(omega*WORLD.getTimeStep())*(position[1] - ICC[1]) + ICC[1];

            angle = angle + omega * WORLD.getTimeStep();
        }

        collisionCheck(newPosition);
    }

    private void updateDustCollection(){
        for(int i = Math.max((int)((position[0] - DIAMETER/2)/DUST_RESOLUTION), 0); i < Math.min((int)((position[0] + DIAMETER/2)/DUST_RESOLUTION) + 2, dustIsCollected.length); i++) {
            for (int j = Math.max((int)((position[1] - DIAMETER/2)/DUST_RESOLUTION),0); j < Math.min((int)((position[1] + DIAMETER/2)/DUST_RESOLUTION) + 2, dustIsCollected[i].length); j++) {
                if(!dustIsCollected[i][j] && dustDistanceSquared(i, j) < Math.pow(DIAMETER/2,2)){
                    dustIsCollected[i][j] = true;
                    totalDustCollected++;
                }
            }
        }
    }

    private double dustDistanceSquared(int i, int j) {
        double dustX = (i + 0.5)*DUST_RESOLUTION;
        double dustY = (j + 0.5)*DUST_RESOLUTION;
        return Math.pow(dustX - position[0],2) + Math.pow((dustY)-position[1],2);
    }

    private void collisionCheck(double[] newPosition){
        boolean collision = false;
        for (double[] wall : WORLD.getEnvironment()) {
            double[] intersect = closestPointOnLine(wall[0], wall[1], wall[2], wall[3], newPosition[0], newPosition[1]);
            double distanceSquared = Math.pow((intersect[0] - newPosition[0]), 2) + Math.pow((intersect[1] - newPosition[1]), 2);
            if (distanceSquared < Math.pow(DIAMETER/2, 2)) {
                collision = true;
                double distance = Math.sqrt(distanceSquared);
                int jumpedWall = lineIntersect(position[0], position[1], newPosition[0], newPosition[1], wall[0], wall[1], wall[2], wall[3]) != null ? -1 : 1;

                newPosition[0] = intersect[0] + (newPosition[0] - intersect[0]) / distance * DIAMETER/2 * jumpedWall;
                newPosition[1] = intersect[1] + (newPosition[1] - intersect[1]) / distance * DIAMETER/2 * jumpedWall;
            }
        }

        if(collision) ticksInWall++;

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

    public void draw(GraphicsContext g){
        double radius = DIAMETER / 2 * GuiSettings.SCALING;
        double x = position[0] * GuiSettings.SCALING;
        double y = position[1] * GuiSettings.SCALING;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        //draw dust
        g.setFill(GuiSettings.DUST_COLOR);
        for (int i = 0; i < dustIsCollected.length; i++) {
            for (int j = 0; j < dustIsCollected[i].length; j++) {
                if(!dustIsCollected[i][j]){
                    g.fillOval((i*DUST_RESOLUTION + DUST_RESOLUTION/2)*GuiSettings.SCALING - 2, (j*DUST_RESOLUTION + DUST_RESOLUTION/2)*GuiSettings.SCALING - 2, 4, 4);
                }
            }
        }

        //draw sensor lines
        g.setStroke(GuiSettings.SENSOR_COLOR);
        g.setLineWidth(1.0);

        for (int i = 0; i < NUM_SENSORS; i++) {
            Vector2D v2 = new Vector2D(angle + SENSOR_ANGLES[i]);
            v2.multiply((sensorValues[i] + DIAMETER/2) * GuiSettings.SCALING);
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

    public NeuralNet getNeuralNet(){
        return NEURAL_NET;
    }

    public String getName() {
        return NAME;
    }

    public int getTotalDustCollected() {
        return totalDustCollected;
    }

    public int getTicksInWall() {
        return ticksInWall;
    }

    public double getFitness() {
        return totalDustCollected*DUST_RESOLUTION*DUST_RESOLUTION/WORLD.getWidth()/WORLD.getHeight() - 1.0*ticksInWall/totalTicks;
    }

    public double[] getPosition() {
        return position;
    }

    public double[] getWheelSpeeds() {
        return wheelSpeeds;
    }

    public void setWheelSpeeds(double leftSpeed, double rightSpeed) {
        if(DO_ACCELERATION){
            wheelSpeeds[0] = clamp(leftSpeed, wheelSpeeds[0] - MAX_ACCELERATION*WORLD.getTimeStep(), wheelSpeeds[0] + MAX_ACCELERATION*WORLD.getTimeStep());
            wheelSpeeds[1] = clamp(rightSpeed, wheelSpeeds[1] - MAX_ACCELERATION*WORLD.getTimeStep(), wheelSpeeds[1] + MAX_ACCELERATION*WORLD.getTimeStep());
        } else {
            wheelSpeeds[0] = leftSpeed;
            wheelSpeeds[1] = rightSpeed;
        }
        clampWheelSpeeds();
    }

    private void clampWheelSpeeds(){
        wheelSpeeds[0] = clamp(wheelSpeeds[0], -MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        wheelSpeeds[1] = clamp(wheelSpeeds[1], -MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
    }

    private static double clamp(double input, double min, double max){
        return Math.min(Math.max(input, min), max);
    }

    public double getAngle() {
        return angle;
    }

    public void reset(){
        reset(WORLD.getStartPosition(), WORLD.getStartAngle());
    }
    public void reset(double[] position, double angle){
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.angle = angle;
        updateSensorValues();
        this.wheelSpeeds = new double[]{0, 0};
        for (boolean[] booleans : dustIsCollected) {
            Arrays.fill(booleans, false);
        }
        this.totalDustCollected = 0;
        this.ticksInWall = 0;
        this.totalTicks = 0;
    }
    @Override
    public int compareTo(Robot otherRobot) {
        return Double.compare(this.getFitness(), otherRobot.getFitness());
    }
}