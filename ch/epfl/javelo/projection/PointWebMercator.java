package epfl.javelo.projection;

public record PointWebMercator(double x, double y) {
    public PointWebMercator {
        if( !(0 <= x && x <= 1) || !(0 <= y && y <= 1)) throw new IllegalArgumentException();
    }

    public static PointWebMercator of(int zoomLevel, double x, double y ) {
        return new PointWebMercator(zoomLevel, zoomLevel);
    }

    public static PointWebMercator ofPointCh(PointCh pointCh) {

    }

    public double xAtZoomLevel() {

    }

    public double yAtZoomLevel() {

    }

    public double lon() {

    }

    public double lat() {

    }

    public PointCh toPointCh() {

    }



}
