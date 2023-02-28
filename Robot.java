
import java.lang.Math;

public class Robot {
    private double[][] environment; //list of all the walls in the world
    final int NUM_SENSORS; //number of sensors
    private final double MAX_SENSOR_DISTANCE; //Maximum distance a sensor can see
    private final double[] SENSOR_ANGLES; //Sensor angles, relative to bot angle
    double[] sensorValues;
    private final double DIAMETER; //diameter
    private final double WHEEL_DISTANCE; //length between wheels
    private double[] position;
    double velRight;
    double velLeft;
    private double angle;

    Robot(int numSensors, double maxSensorDistance, double diameter, double wheelDistance, double[] position, double velLeft, double velRight, double angle){
        this.NUM_SENSORS = numSensors;
        this.MAX_SENSOR_DISTANCE = maxSensorDistance;
        SENSOR_ANGLES = new double[numSensors];
        sensorValues = new double[numSensors];
        createSensors();
        this.DIAMETER = diameter;
        this.WHEEL_DISTANCE = wheelDistance;

        this.position = position;
        this.velLeft = velLeft;
        this.velRight = velRight;
        this.angle = angle;
    }

    Robot(double[] position, double angle) {
        this(12, 10, .5, .4, position, 0.0, 0.0, angle);
    }

    public void createSensors(){
        double angleInterval = 2*Math.PI/ NUM_SENSORS;

        for(int i = 0; i < NUM_SENSORS; i++) {
            SENSOR_ANGLES[i] = angle + i*angleInterval;
        }
    }

    public void updateSensorValues() {
        // update sensor values
        if (environment != null){
            for (int i = 0; i < NUM_SENSORS; i++) {
                sensorValues[i] = calculateSensorValue(angle + SENSOR_ANGLES[i]);
            }
        }
    }

    public double calculateSensorValue(double sensorAngle) {
        double sensorX = position[0] + MAX_SENSOR_DISTANCE * Math.cos(sensorAngle);
        double sensorY = position[1] + MAX_SENSOR_DISTANCE * Math.sin(sensorAngle);
        double minimumSquared = Math.pow(MAX_SENSOR_DISTANCE, 2);
        for (double[] wall : environment) {
            double[] intersect = lineIntersect(position[0], position[1], sensorX, sensorY, wall[0], wall[1], wall[2], wall[3]);
            if (intersect != null) {
                double distanceSquared = Math.pow((intersect[1] - position[1]), 2) + Math.pow((intersect[0] - position[0]), 2);
                if (minimumSquared > distanceSquared) {
                    minimumSquared = distanceSquared;
                }
            }
        }
        return Math.sqrt(minimumSquared - DIAMETER/2);
    }

    public void updatePosition(double timeStep){
        double newX;
        double newY;

        if (velLeft == velRight) {
            newX = position[0] + Math.cos(angle) * velLeft * timeStep;
            newY = position[1] + Math.sin(angle) * velLeft * timeStep;
        } else {
            double radius = (WHEEL_DISTANCE / 2) * ((velLeft + velRight) / (velRight - velLeft));
            double omega = (velRight - velLeft) / WHEEL_DISTANCE;

            double[] ICC = {position[0] - radius * Math.sin(angle), position[1] + radius * Math.cos(angle)};
            newX = Math.cos(omega*timeStep)*(position[0] - ICC[0]) - Math.sin(omega*timeStep)*(position[1] - ICC[1]) + ICC[0];
            newY = Math.sin(omega*timeStep)*(position[0] - ICC[0]) + Math.cos(omega*timeStep)*(position[1] - ICC[1]) + ICC[1];

            angle = angle + omega * timeStep;
        }

        collisionCheck(newX, newY);
        updateSensorValues();
    }

    public void collisionCheck(double newX, double newY){
        for (double[] wall : environment) {
            double[] intersect = closestPointToLine(wall[0], wall[1], wall[2], wall[3], newX, newY);
            double distanceSquared = Math.pow((intersect[1] - position[1]), 2) + Math.pow((intersect[0] - position[0]), 2);
            if (distanceSquared < Math.pow(DIAMETER/2, 2)) {
                double length = Math.sqrt(distanceSquared);
                newX = intersect[0] + (newX - intersect[0]) / length * DIAMETER/2;
                newY = intersect[1] + (newY - intersect[1]) / length * DIAMETER/2;
            }
        }

        position[0] = newX;
        position[1] = newY;
    }

    public double[] closestPointToLine(double x1, double y1, double x2, double y2, double pointX, double pointY) {
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

    public double[] lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        // Lines are parallel.
        if (denom == 0.0) return null;

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
        if (ua > 0.0f && ua < 1.0f && ub > 0.0f && ub < 1.0f) {
              // Get the intersection point.
              return new double[]{(x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1))};
        }
      
        return null;
    }

    public void updateEnvironment(double[][] environment) {
        this.environment = environment;
        updateSensorValues();
    }
}
