package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {

    @Test
    public void positionClosestToWorks() throws IOException {
        PointCh p1 = new PointCh(2_533_132, 1_152_206);
        PointCh p2 = new PointCh( 2_533_513.610, 1_152_248.664);
        PointCh p3 = new PointCh( 2_533_232.407, 1_152_164.508);
        Edge e = new Edge(0,0, p1, p2, 384.2, null);
        double expected = 0;
        assertEquals(expected, e.positionClosestTo(p1));
        assertEquals(94.14, e.positionClosestTo(p3), 3);
    }

    @Test
    public void pointAtWorks() {
        PointCh p1 = new PointCh(2_533_132, 1_152_206);
        PointCh p2 = new PointCh( 2_533_513.610, 1_152_248.664);
        Edge e = new Edge(0,0, p1, p2, 384.2, null);
        assertEquals(new PointCh( 2_533_232.407, 1_152_164.508), e.pointAt(95.13));
    }
}
