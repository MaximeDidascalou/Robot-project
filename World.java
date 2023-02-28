public class World {
    final double WIDTH;
    final double HEIGHT;
    double[][] environment;
    Robot r;

    World(double width, double height, Robot r){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.environment = createEnvironment();

        r.updateEnvironment(environment);
        this.r = r;
    }

    private double[][] createEnvironment() {
        double[] top = new double[] {0, HEIGHT, WIDTH, HEIGHT};
        double[] bottom = new double[] {0, 0, WIDTH, 0};
        double[] left = new double[] {0, 0, 0, HEIGHT};
        double[] right = new double[] {WIDTH, 0, WIDTH, HEIGHT};
        return new double[][] {bottom, top, left, right};
    }
}
