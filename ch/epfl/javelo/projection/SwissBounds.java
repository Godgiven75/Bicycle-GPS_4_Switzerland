package epfl.javelo.projection;

public final class SwissBounds {
    private SwissBounds() {}

    public final double MIN_E = 2_485_000;
    public final double MAX_E = 2_834_000;
    public final double MIN_N = 1_075_000;
    public final double MAX_N = 1_296_000;
    public final double WIDTH = MAX_E - MIN_E;
    public final double HEIGHT = MAX_N - MIN_N;

    public static boolean containsEN(double e, double n) {
        return ((MIN_E < e)&&(e < MAX_E) && (MIN_N < n)&&(n < MAX_N));
    }
}
