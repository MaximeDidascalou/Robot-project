import java.util.Arrays;

public class Main {
    public static void main(String []args) {
        double[] initialPosition = {1.0, 1.0};
        double initialAngle = Math.PI/6;
        Robot robot = new Robot(initialPosition, initialAngle);
        World world = new World(3.0,3.0, 0.1, robot);

        for (int i = 0; i < 200; i++) {
            world.step();
        }

//        for (int i = 0; i < r1.NUM_SENSORS; i++) {
//            System.out.println(r1.sensorValues[i]);
//        }
//        System.out.println(lineIntersect(0,0,1,1,0,1,1,0)[0]);
//    }
//
//    public static double[] lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
//        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
//        // Lines are parallel.
//        if (denom == 0.0) return null;
//
//        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
//        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
//        if (ua > 0.0f && ua < 1.0f && ub > 0.0f && ub < 1.0f) {
//              // Get the intersection point.
//              return new double[]{(x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1))};
//        }
//
//        return null;
    }
}
