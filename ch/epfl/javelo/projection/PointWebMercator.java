package epfl.javelo.projection;

public record PointWebMercator(double x, double y) {

    private static final int BASE_ZOOM_LEVEL = 8;

    public PointWebMercator {
        if( !(0 <= x && x <= 1) || !(0 <= y && y <= 1)) throw new IllegalArgumentException();
    }

    public static PointWebMercator of(int zoomLevel, double x, double y ) {
        if(!(zoomLevel >= 0 && zoomLevel <= 20)) throw new IllegalArgumentException();

        double scaledX = Math.scalb(x, - (zoomLevel + BASE_ZOOM_LEVEL));
        double scaledY = Math.scalb(y, - (zoomLevel + BASE_ZOOM_LEVEL));
        return new PointWebMercator(scaledX, scaledY);
    }

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double longitude = pointCh.lon();
        double latitude = pointCh.lat();

        double x = WebMercator.x(longitude);
        double y = WebMercator.y(latitude);

        return new PointWebMercator(x, y);
    }

    public double xAtZoomLevel(int zoomLevel) {
        if(!(zoomLevel >= 0 && zoomLevel <= 20)) throw new IllegalArgumentException();
        return Math.scalb(x, zoomLevel + BASE_ZOOM_LEVEL );
    }

    public double yAtZoomLevel(int zoomLevel) {
        if(!(zoomLevel >= 0 && zoomLevel <= 20)) throw new IllegalArgumentException();
        return Math.scalb(y, zoomLevel + BASE_ZOOM_LEVEL );
    }

    public double lon() {
        return WebMercator.lon(x);
    }

    public double lat() {
        return WebMercator.lat(y);
    }

    public PointCh toPointCh() {
        double lon = lon();
        double lat = lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);

        return SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null;
    }
}
