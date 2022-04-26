package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.file.Path;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;

public class GpxGeneratorTest {

    public static void main(String[] args) throws IOException {
        Path basePath = Path.of("lausanne");
        Graph graph = Graph.loadFrom(basePath);
        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        Route route = routeComputer.bestRouteBetween(159049 , 117669);
        ElevationProfile elevationProfile = elevationProfile(route, 5);
        GpxGenerator.createGpx(route, elevationProfile);
        GpxGenerator.writeGpx("test", route, elevationProfile);
    }
}
