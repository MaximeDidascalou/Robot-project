package com.three.ars_2.gui;

public class Vector2D {
    public double x,y;
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Vector2D(double angle){
        x = Math.cos(angle);
        y = Math.sin(angle);
    }
    public void multiply(double m){
        x = x*m;
        y = y*m;
    }
}
