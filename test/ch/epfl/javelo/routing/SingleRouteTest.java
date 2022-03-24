package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleRouteTest {

    @Test
    public void pointAtWorks() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300, null);
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100, null);
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200, null);
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700, null);
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        assertEquals(p.e(), s.pointAt(10_000).e(), 3);
        assertEquals(p.n(), s.pointAt(10_000).n(), 3);
    }

    @Test
    public void lengthWorks() {
        var rng = newRandom();
        List<Edge> l = new ArrayList<>();
        double totalLength = 0;
        for(int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double length = rng.nextDouble();
            totalLength += length;
            double eFrom = newRandom().nextDouble(SwissBounds.MIN_E, SwissBounds.MAX_E);
            double nFrom = newRandom().nextDouble(SwissBounds.MIN_N, SwissBounds.MAX_N);
            double eTo = newRandom().nextDouble(SwissBounds.MIN_E , SwissBounds.MAX_E);
            double nTo = newRandom().nextDouble(SwissBounds.MIN_N, SwissBounds.MAX_N);
            PointCh fromPoint = new PointCh(eFrom, nFrom);
            PointCh toPoint = new PointCh(eTo, nTo);

            l.add(new Edge(i, i + 1, fromPoint, toPoint, length, null));
        }
        SingleRoute sr = new SingleRoute(l);
        double expected = totalLength;
        double actual = sr.length();
        assertEquals(expected, actual);
    }

    @Test
    public void nodeClosestToWorks() {
        double[] positions = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0, 1, null, null, 5800, null );
        Edge e1 = new Edge(1, 2, null, null,  2300, null);
        Edge e2 = new Edge (2, 3 ,null, null,   1100, null);
        Edge e3 = new Edge (3, 4, null, null ,  2200, null);
        Edge e4 = new Edge (4, 5, null, null, 1700 , null);

        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute sr = new SingleRoute(l);

        int expected = 0;
        int actual = sr.nodeClosestTo(1);
        assertEquals(expected, actual);

        expected = 0;
        actual = sr.nodeClosestTo(2900);
        assertEquals(expected, actual);

        expected = 5;
        actual = sr.nodeClosestTo(13100);
        assertEquals(expected, actual);

    }

}
