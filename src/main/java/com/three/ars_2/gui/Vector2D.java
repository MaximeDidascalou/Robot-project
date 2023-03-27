//////
////
//// vector with 2 elements class
////
//// This file was written by Cavid Karca
////
//////

package com.three.ars_2.gui;

public class Vector2D {
    public double x,y;
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    // get angle
    public Vector2D(double angle){
        x = Math.cos(angle);
        y = Math.sin(angle);
    }

    // multiply with another vector
    public Vector2D multiply(double m){
        x = x*m;
        y = y*m;
        return this;
    }

    // get length
    public double length(){
        return Math.sqrt(x*x + y*y);
    }

    // normalise (size 1)
    public Vector2D normalize(){
        double l = this.length();
        x = x/l;
        y = y/l;
        return this;
    }

    // add another vector
    public Vector2D add(Vector2D v2){
        x = x + v2.x;
        y = y + v2.y;
        return this;
    }

    // subtract another vector
    public Vector2D subtract(Vector2D v2){
        x = x - v2.x;
        y = y - v2.y;
        return this;
    }

    // return a copy of vector
    public Vector2D clone(){
        return new Vector2D(x,y);
    }

    // for printing
    public String toString(){
        return x+","+y+"\n";
    }
}
