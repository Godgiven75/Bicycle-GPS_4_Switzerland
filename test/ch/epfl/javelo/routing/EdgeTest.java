package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class EdgeTest {

    @Test
    public void positionClosestToWorks() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);
        PointCh p = new PointWebMercator(WebMercator.x(Math.toRadians(6.6013034)), WebMercator.y(Math.toRadians(46.6326106));
        Edge e = new Edge()
        double expected = 0;
        assertEquals(expected, )

    }
}
