package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
}
