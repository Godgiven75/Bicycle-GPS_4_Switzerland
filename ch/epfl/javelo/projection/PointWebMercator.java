package epfl.javelo.projection;

public record PointWebMercator(double x, double y) {
    public PointWebMercator {
        if( !(0 <= x && x <= 1) || !(0 <= y && y <= 1)) throw new IllegalArgumentException();
    }

    public static PointWebMercator of(int zoomLevel, double x, double y ) {
        double imageSize = Math.pow(2, 8 + zoomLevel);
        return new PointWebMercator(x/imageSize, y/imageSize);
    }

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double longitude = pointCh.lon();
        double latitude = pointCh.lat();

        double x = WebMercator.x(longitude);
        double y = WebMercator.y(latitude);

        return new PointWebMercator(x, y);
    }

    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, zoomLevel + 8 );
    }

    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, zoomLevel + 8 );

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
