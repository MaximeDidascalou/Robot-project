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
    public Vector2D multiply(double m){
        x = x*m;
        y = y*m;
        return this;
    }

    public double length(){
        return Math.sqrt(x*x + y*y);
    }

    public Vector2D normalize(){
        double l = this.length();
        x = x/l;
        y = y/l;
        return this;
    }
    public Vector2D add(Vector2D v2){
        x = x + v2.x;
        y = y + v2.y;
        return this;
    }
    public Vector2D subtract(Vector2D v2){
        x = x - v2.x;
        y = y - v2.y;
        return this;
    }
    public Vector2D clone(){
        return new Vector2D(x,y);
    }
    public String toString(){
        return x+","+y+"\n";
    }
}
