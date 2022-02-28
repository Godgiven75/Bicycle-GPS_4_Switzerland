package ch.epfl.javelo.projection;

import epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static java.util.stream.DoubleStream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointWebMercatorTest {

    // Constructeur
    @Test
    public void isExceptionThrown() {

    }

    @Test
    public void checkOf() {
        PointWebMercator a = new PointWebMercator(69_561_722, 47_468_099);
        assertEquals(a, of(19, 0.518275214444, 0.353664894749));
    }
}
