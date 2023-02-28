public class Simul {
    void simulate_timestep(World w, char input, double speed_change) {
        double vl = w.r.velLeft;
        double vr = w.r.velRight;
        if (input == 'w'){
            vl = vl + speed_change;
        }
        else if (input == 's'){
            vl = vl - speed_change;
        }
        else if (input == 'o'){
            vr = vr + speed_change;
        }
        else if (input == 'l'){
            vr = vr - speed_change;
        }
        else if (input == 'x'){
            vr = 0;
            vl = 0;
        }
        else if (input == 't'){
            vr = vr + speed_change;
            vl = vl + speed_change;
        }
        else if (input == 'g'){
            vr = vr - speed_change;
            vl = vl + speed_change;
        }
        w.r.velLeft = vl;
        w.r.velRight = vr;
        w.r.updatePosition(0.1);
    }
}
