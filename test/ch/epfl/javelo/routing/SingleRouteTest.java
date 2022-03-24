package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.isNaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void pointAtWorks2() {
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
        PointCh p = e2.pointAt(1);
        assertEquals(p.e(), s.pointAt(1).e(), 4);
        assertEquals(p.n(), s.pointAt(1).n(), 4);
    }

    @Test
    public void elevationAtReturnsNaNIfNoProfile(){
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        assertTrue(isNaN(s.elevationAt(10_000)));
    }

    @Test
    public void elevationAtReturnsAltitudeAtGivenPosition() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0, 0, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199), 5800,
                Functions.constant(4589));
        Edge e1 = new Edge(0, 0, e0.pointAt(100), e0.pointAt(200), 2300,
                Functions.constant(234));
        Edge e2 = new Edge(0, 0, e1.pointAt(200), e1.pointAt(300), 1100,
                Functions.constant(643));
        Edge e3 = new Edge(0, 0, e2.pointAt(300), e2.pointAt(400), 2200,
                Functions.constant(0));
        Edge e4 = new Edge(0, 0, e3.pointAt(400), e3.pointAt(500), 1700,
                Functions.constant(2.3));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        double expected = 234;
        assertEquals(expected, s.elevationAt(5_900));
    }

    @Test
    public void elevationAtWorksWithNonConstantAltitudesOnInterval() {

        float[] elevationSamples = new float[]{384.75f, 384.6875f, 384.5625f, 384.5f};
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.sampled(elevationSamples, 2300));
        Edge e2 = new Edge(2,3,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        float expected = 384.5f;
        assertEquals(expected, s.elevationAt(5900));
    }

    @Test
    public void pointClosestToWorksOnTrivialPoint() {
       PointCh p = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
       RoutePoint rp = new RoutePoint(p, 0, 0);
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
       assertEquals(rp, s.pointClosestTo(p));
    }

    @Test
    public void pointClosestToWorksOnHorizontalItinerary() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N);

        RoutePoint rp = new RoutePoint(pp, 0, p.distanceTo(pp));

        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }
}
