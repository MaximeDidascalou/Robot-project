package com.three.ars_2.simulation;

import com.three.ars_2.gui.Vector2D;

import java.util.ArrayList;
import java.util.Random;

public class Particle {
    public double a,b,c;
    public Vector2D pos,vel,best_pos;
    public static Vector2D global_best_pos;
    public static double global_best_performance = Double.MAX_VALUE;
    public double performance = Double.MAX_VALUE;
    private double maxSpeed;
    private Random random = new Random();
    public ArrayList<Vector2D> position_history = new ArrayList<>();

    public Particle(Vector2D pos, Vector2D vel, double max_speed) {
       this(0.9,2,1,pos,vel,max_speed);
    }

    public Particle(double a, double b, double c, Vector2D pos, Vector2D vel, double max_speed) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.pos = pos;
        this.vel = vel;
        this.maxSpeed = max_speed;
        best_pos = pos.clone();
        position_history.add(pos.clone());
    }

    public void updateVelocity(){
        // new vel
        Vector2D vb = best_pos.clone();
        Vector2D vc = global_best_pos.clone();
        vb.subtract(pos).multiply(random.nextDouble()*b);
        vc.subtract(pos).multiply(random.nextDouble()*c);
        vel.multiply(a).add(vb).add(vc);
        // Max speed
        if(vel.length() > maxSpeed){
            vel.normalize().multiply(maxSpeed);
        }
        //
    }
    public void updatePosition(){
        pos.add(vel);
        position_history.add(pos.clone());
        performance = getPerformance(pos.x, pos.y);
        if(performance < global_best_performance){
            global_best_performance = performance;
            global_best_pos = pos.clone();
        }
    }

    public double getPerformance( double x, double y){
        double rosenbrock = ((0-x)*(0-x)) + (100*Math.pow(y-x*x,2));
        return rosenbrock;
    }
}
