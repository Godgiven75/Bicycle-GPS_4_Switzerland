package ch.epfl.javelo.projection;

import epfl.javelo.Math2;
import epfl.javelo.projection.PointCh;
import epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static epfl.javelo.projection.PointWebMercator.ofPointCh;
import static java.util.stream.DoubleStream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals(new PointWebMercator(0.518275214444, 0.353664894749), ofPointCh(new PointCh(2.69221 * 1e6, 1.15237 * 1e6)));
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
        assertEquals(Math.scalb(0.353664894749, -19) , p.yAtZoomLevel(19));
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
        assertEquals(Math.atan( Math2.asinh(Math.PI - 2.0 * Math.PI * 0.353664894749) ), p.lat());
    }

    // Méthode toPointCh
    @Test
    public void

}
