package ch.epfl.javelo.projection;

import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.projection.WebMercator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {

    @Test
    public void checkX() {
        assertEquals(0.518275214444, x(6.5790772));
    }

    @Test
    public void checkY() {
        assertEquals(0.353664894749, y(46.5218976));
    }

    @Test
    public void checkLon() {
        assertEquals(6.5790772, lon(0.518275214444));
    }

    @Test
    public void checkLat() {
        assertEquals(46.5218976, lat(0.353664894749));
    }
}
