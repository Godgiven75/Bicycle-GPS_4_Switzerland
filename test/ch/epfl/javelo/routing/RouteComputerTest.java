package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class RouteComputerTest {

    @Test
    void bestRouteBetweenWorksWithDijkstraOnLausanne() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route bestRoute = rc.bestRouteBetween(159049, 117669);
        double expected = 9588.5625;
        double actual = bestRoute.length();
        assertEquals(expected, actual);
    }
    @Test
    void bestRouteBetweenWorksWithDisjkstraOnCH_West() throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route bestRoute = rc.bestRouteBetween( 2046055, 2694240);
        double expected = 168378.875;
        double actual = bestRoute.length();
        assertEquals(expected, actual);
    }


    @Test
    void bestTrouteThrowsOnIllegalItinerary() throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            var rng = newRandom();
            int nodeIndex = rng.nextInt(3714919);
            assertThrows(IllegalArgumentException.class , () -> {
                rc.bestRouteBetween( nodeIndex, nodeIndex);
            });
        }


    }
    @Test
    void bestRouteReturnsNullWithNonExistingItinerary() throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route bestRoute = rc.bestRouteBetween(3714918, 3714919);
        assertNull(bestRoute);
    }

}
