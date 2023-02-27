
import java.lang.Math;

public class Robot {
    int num_sensors;
    double width; // diameter or length between wheels
    double[] sensor_angles;
    double[] sensor_values;
    double[][] environment;
    
    double[] position;
    double vel_right;
    double vel_left;
    double angle;


    Robot(int num_sensors, double width, double[] position,
          double vel_left, double vel_right, double angle){
        this.num_sensors = num_sensors;
        this.width = width;
        this.position = position;
        this.vel_left = vel_left;
        this.vel_right = vel_right;
        this.angle = angle;

        sensor_angles = new double[num_sensors];
        sensor_values = new double[num_sensors];
        update_sensors();
    }

    void update_sensors() {
        // update sensor angles
        double shift = 360 / num_sensors;
        double total_shift = 0;
        for (int i = 0; i < num_sensors; i++) {
            sensor_angles[i] = angle + total_shift;
            total_shift += shift;
        }

        // update sensor values
        if (environment != null){
            for (int i = 0; i < num_sensors; i++) {
                double rad_angle = Math.toRadians(sensor_angles[i]);
                double x2 = position[0] + 100 * Math.cos(rad_angle);
                double y2 = position[1] + 100 * Math.sin(rad_angle);
                double min = 100;
                for (int j = 0; j < environment.length; j++) {
                    double[] intersect = lineIntersect(position[0], position[1], x2, y2, 
                                                    environment[j][0], environment[j][1],environment[j][2],environment[j][3]);
                    if (intersect != null){
                        double distance = Math.pow((intersect[1] - position[1]), 2) + Math.pow((intersect[0] - position[0]),2);
                        if (min > distance) {
                            min = distance;
                        }
                    }
                }
                sensor_values[i] = Math.sqrt(min);
            }
        }
    }
    double[] lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0.0) { // Lines are parallel.
           return null;
        }
        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
          if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
              // Get the intersection point.
              return new double[]  {(x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1))};
          }
      
        return null;
    }

}
