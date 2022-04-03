package ch.epfl.javelo.projection;


import org.junit.jupiter.api.Test;


import static ch.epfl.javelo.projection.PointWebMercator.ofPointCh;
import static ch.epfl.javelo.projection.WebMercator.*;
import static org.junit.jupiter.api.Assertions.*;

public final class PointWebMercatorTest {

    // Constructeur
    @Test
    public void isExceptionThrownWhenArgumentOutOfInterval() {
        assertThrows(IllegalArgumentException.class, () -> {new PointWebMercator(2,0.5);});
    }

    // areCoordinatesWellScaled()
    /*
    public static void main(String[] args) {
        PointWebMercator a = new PointWebMercator(Math.scalb(0.518275214444, -27), Math.scalb(0.353664894749, -27));
        PointWebMercator b = of(19, 0.518275214444, 0.353664894749);
        System.out.println(a.x + " " + b.x);
    }
     */

    // Méthode PointCh marche
    @Test
    public void isPointCHWellConvertedToWebMercator() {
       // assertEquals(new PointWebMercator(0.518275214444, 0.353664894749), ofPointCh(new PointCh(2.69221 * 1e6, 1.15237 * 1e6)));
    }

    // Méthode xAtZoomLevel
    @Test
    public void isXAtTheRightZoomLevel() {
        PointWebMercator p = new PointWebMercator(0.518275214444, 0.353664894749);
        assertEquals(Math.scalb(0.518275214444, 27) , p.xAtZoomLevel(19));
    }

    // Méthode yAtZoomLevel
    @Test
    public void isYAtTheRightZoomLevel() {
        PointWebMercator p = new PointWebMercator(0.518275214444, 0.353664894749);
        assertEquals(Math.scalb(0.353664894749, 27) , p.yAtZoomLevel(19));
    }

    // Méthode lon
    @Test
    public void retourneLongitudeEnRad() {
        PointWebMercator p = new PointWebMercator(0.518275214444, 0.353664894749);
        assertEquals(2.0 * Math.PI * 0.518275214444 - Math.PI , p.lon());
    }

    // Méthode lat
    @Test
    public void retourneLatitudeEnRad() {
        PointWebMercator p = new PointWebMercator(0.518275214444, 0.353664894749);
        assertEquals(Math.atan( Math.sinh(Math.PI - 2.0 * Math.PI * 0.353664894749) ), p.lat());
    }

    // Méthode toPointCh
    @Test
    public void doesReturnSwissCoordinatesOfSamePoint() {
        double x = 0.518275214444;
        double y = 0.353664894749;
        PointWebMercator p = new PointWebMercator(x,y);
        double lon = 2.0 * Math.PI * x - Math.PI;
        double lat = Math.atan( Math.sinh(Math.PI - 2.0 * Math.PI * y) );
        System.out.println(lon + " " + lat);
        double l1 = (1e-4) * (3600 * Math.toDegrees(lon) - 26782.5);
        double phi1 = (1e-4) * (3600 * Math.toDegrees(lat) - 169028.66);
        double e = 2_600_072.37 + 211_455.93 * l1 - 10_938.51 * l1 * phi1 - 0.36 * l1 * phi1 * phi1 - 44.54 * l1 * l1 * l1;
        double n = 1_200_147.07 + 308_807.95 * phi1 + 3_745.25 * l1 *l1 + 76.63 * phi1 * phi1 - 194.56 * l1 * l1 * phi1 + 119.79 * phi1 * phi1 * phi1;
        System.out.println(e + " " + n);
        assertEquals(new PointCh(e,n), p.toPointCh());
    }


    @Test
    public void doesReturnNullIfThisIsNotInSwissBounds() {
        double lon = Ch1903.lon(2_485_001, 1_075_000);
        double lat = Ch1903.lat(2_485_001, 1_075_001);
        PointWebMercator p = new PointWebMercator(x(lon), y(lat));
        assertNull(p.toPointCh());
    }

}
