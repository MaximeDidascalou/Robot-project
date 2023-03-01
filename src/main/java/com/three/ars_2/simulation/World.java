package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class World {
    private double height;
    private double width;
    private ArrayList<Robot> robots = new ArrayList<>();

    private double[][] environment;
    Robot r;
    public World(double width, double height) {
        this.height = height;
        this.width = width;
        double[] pos = new double[]{10,10};
        robots.add(new Robot(pos,0,"Robot 1"));
        environment = createEnvironment();
        for(Robot r: this.getRobots()){
            r.updateEnvironment(environment);
        }
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public double[][] getEnvironment(){ return environment; }
    private double[][] createEnvironment() {
        double[] top = new double[] {0, height, width, height};
        double[] bottom = new double[] {0, 0, width, 0};
        double[] left = new double[] {0, 0, 0, height};
        double[] right = new double[] {width, 0, width, height};
        //double[] middle = new double[] {300,300,500,500};
        return new double[][] {bottom, top, left, right};
    }

    public void draw(GraphicsContext g){
        g.setFill(GuiSettings.backgroundColor);
        g.fillRect(0,0,getWidth()*GuiSettings.scaling,getHeight()*GuiSettings.scaling);
        g.setFill(GuiSettings.wall);
        double[][] environment = getEnvironment();
        for(double[] d: environment){
            g.fillRect(d[0]*GuiSettings.scaling,d[1]*GuiSettings.scaling,(d[2]-d[0])*GuiSettings.scaling,(d[3]-d[1])*GuiSettings.scaling);
        }
    }
}
