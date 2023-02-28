public class World {
    final double WIDTH;
    final double HEIGHT;
    double[][] environment;

    double timeStep;
    Robot robot;

    World(double width, double height, double timeStep, Robot robot){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.environment = createEnvironment();

        this.timeStep = timeStep;

        robot.updateEnvironment(environment);
        this.robot = robot;
    }

    public void step(){
        robot.update(timeStep);
    }

    private double[][] createEnvironment() {
        double[] top = new double[] {0, HEIGHT, WIDTH, HEIGHT};
        double[] bottom = new double[] {0, 0, WIDTH, 0};
        double[] left = new double[] {0, 0, 0, HEIGHT};
        double[] right = new double[] {WIDTH, 0, WIDTH, HEIGHT};
        double[] wall = new double[] {2, 1, 2, 2};
        return new double[][] {bottom, top, left, right, wall};
    }
}
