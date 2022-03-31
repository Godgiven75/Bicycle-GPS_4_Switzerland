package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage6Test {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        //Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        //Route r = rc.bestRouteBetween(159049, 117669);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        long t0 = System.nanoTime();
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
        KmlPrinter.write("javelo.kml", r);
    }
}

