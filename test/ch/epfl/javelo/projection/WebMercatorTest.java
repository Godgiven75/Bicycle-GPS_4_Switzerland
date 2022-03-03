package ch.epfl.javelo.projection;

import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.projection.WebMercator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {

    @Test
    public void checkX() {
        assertEquals(0.518275214444, x(Math.toRadians(6.5790772)), 1e-7);
    }

    @Test
    public void checkY() {
        assertEquals(0.353664894749, y(Math.toRadians(46.5218976)), 1e-7);
    }

    @Test
    public void checkLon() {
        assertEquals(Math.toRadians(6.5790772), lon(0.518275214444), 1e-7);
    }

    @Test
    public void checkLat() {
        assertEquals(Math.toRadians(46.5218976), lat(0.353664894749), 1e-7);
    }
}
