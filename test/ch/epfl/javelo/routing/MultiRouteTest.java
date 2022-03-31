package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Double.isNaN;
import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.*;

public class MultiRouteTest {

    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    @Test
    void indexOfSegmentAtWorksWithOnlySimpleRoutes() {
        Edge e = new Edge(0, 1, null, null, 1000, Functions.constant(NaN));
        SingleRoute sr0 = new SingleRoute(List.of(e));
        SingleRoute sr1 = new SingleRoute(List.of(e));
        SingleRoute sr2 = new SingleRoute(List.of(e));
        MultiRoute mr = new MultiRoute(List.of(sr0, sr1, sr2));
        int actual1 = mr.indexOfSegmentAt(-500);
        int expected1 = 0;
        assertEquals(expected1, actual1);

        int actual2 = mr.indexOfSegmentAt(100);
        int expected2 = 0;
        assertEquals(expected2, actual2);
        int actual3 = mr.indexOfSegmentAt(1500);
        int expected3 = 1;
        assertEquals(expected3, actual3);

        int actual4 = mr.indexOfSegmentAt(3000);
        int expected4 = 2;
        assertEquals(expected4, actual4);

        int actual5 = mr.indexOfSegmentAt(4500);
        int expected5 = 2;
        assertEquals(expected5, actual5);
    }

    @Test
    void indexOfSegmentAtWorksWithMultiRoutesAndSingleRoutes1() {
        Edge e = new Edge(0, 1, null, null, 1000, Functions.constant(NaN));
        SingleRoute sr0 = new SingleRoute(List.of(e));
        SingleRoute sr1 = new SingleRoute(List.of(e));
        SingleRoute sr2 = new SingleRoute(List.of(e));
        MultiRoute mr1 = new MultiRoute(List.of(sr0, sr1, sr2));
        MultiRoute mr2 = new MultiRoute(List.of(sr0, sr1, sr2));
        MultiRoute m = new MultiRoute(List.of(mr1, mr2));

        int expected1 = 0;
        int actual1 = m.indexOfSegmentAt(-1000);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = m.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);

        int expected3 = 0;
        int actual3 = m.indexOfSegmentAt(750);
        assertEquals(expected3, actual3);

        int expected4 = 2;
        int actual4 = m.indexOfSegmentAt(2100);
        assertEquals(expected4, actual4);

        int expected5 = 5;
        int actual5 = m.indexOfSegmentAt(5500);
        assertEquals(expected5, actual5);

        int expected6 = 5;
        int actual6 = m.indexOfSegmentAt(76098);
        assertEquals(expected6, actual6);
    }

    @Test
    void indexOfSegmentAtWorksWithMultiRoutesAndSingleRoutes2() {
        Edge e300 = new Edge(0, 1, null, null, 300, Functions.constant(NaN));
        Edge e400 = new Edge(1, 2, null, null, 400, Functions.constant(NaN));
        SingleRoute s300 = new SingleRoute(List.of(e300));
        SingleRoute s400 = new SingleRoute(List.of(e400));
        SingleRoute s700 = new SingleRoute(List.of(e300, e400));

        MultiRoute mr1 = new MultiRoute(List.of(s300, s400));
        MultiRoute mr2 = new MultiRoute(List.of(mr1, s700));
        MultiRoute mr3 = new MultiRoute(List.of(s300, mr1, mr2, s300, s700));

        int expected1 = 0;
        int actual1 = mr3.indexOfSegmentAt(-9073.5);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = mr3.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);

        int expected3 = 2;
        int actual3 = mr3.indexOfSegmentAt(1000);
        assertEquals(expected3, actual3);

        int expected4 = 5;
        int actual4 = mr3.indexOfSegmentAt(2000);
        assertEquals(expected4, actual4);

        int expected5 = 6;
        int actual5 = mr3.indexOfSegmentAt(2500);
        assertEquals(expected5, actual5);

        int expected6 = 7;
        int actual6 = mr3.indexOfSegmentAt(3400);
        assertEquals(expected6, actual6);
    }

    @Test
    void lengthWorks() {
        var rng = newRandom();
        double edgeLength = newRandom().nextDouble(100000);
        Edge e = new Edge(0, 1, null, null, edgeLength, Functions.constant(NaN));

        int nbEdges = rng.nextInt(10000);
        List<Route> segments = new ArrayList<>();
        for (int i = 0; i < nbEdges; i++) {
            segments.add(new SingleRoute(List.of(e)));
        }
        MultiRoute mr = new MultiRoute(segments);
        double delta = mr.length() / 1000000000;
        System.out.println("La précision de l'égalité suivante est de l'ordre de " +  delta);
        assertEquals(mr.length(), nbEdges * edgeLength, delta);
    }

    @Test
    void edgesWorks() {
        List<Edge> edgesList = randomListOfEdges(10000);
        Route r1 = new SingleRoute(edgesList);
        Route r2 = new MultiRoute(List.of(r1));
        assertArrayEquals(r2.edges().toArray(new Edge[0]), r1.edges().toArray(new Edge[0]));

        Route r3 = new MultiRoute(List.of(r1, r2));
        List<Edge> l = new ArrayList<>();
        l.addAll(r1.edges());
        l.addAll(r2.edges());
        assertArrayEquals(r3.edges().toArray(new Edge[0]), l.toArray(new Edge[0]));
    }

    List<Edge> randomListOfEdges(int nbEdges) {
        var rng = newRandom();
        List<Edge> l = new ArrayList<>();
        for (int i = 0; i < nbEdges; i++) {
            double edgeLength = rng.nextDouble(1000);
            Edge e = new Edge(i, i + 1, randomPointCh(), randomPointCh(), edgeLength, Functions.constant(NaN));
            l.add(e);
        }
        return l;
    }

    PointCh randomPointCh() {
        var rng = new Random();
        return new PointCh(rng.nextDouble(SwissBounds.MIN_E, SwissBounds.MAX_E), rng.nextDouble(SwissBounds.MIN_N, SwissBounds.MAX_N));
    }

    @Test
    boolean pointsWorks() {
        boolean works = true;
        List<Edge> edgesList = randomListOfEdges(10000);
        Route r1 = new SingleRoute(edgesList);
        Route r2 = new MultiRoute(List.of(r1));
        Route r3 = new MultiRoute(List.of(r1, r2));
        List<PointCh> r1Points = r1.points();
        List<PointCh> r2Points = r2.points();
        List<PointCh> r3Points = r3.points();
        r1Points.addAll(r2Points);
        for (int i = 0; i < r3Points.size(); i++) {
            PointCh r3Point = r3Points.get(i);
            PointCh r1Point = r1Points.get(i);
            if (!r1Point.equals(r3Point))
                works = false;
            assertEquals(r3Point, r1Point);
        }
        return works;
    }

    @Test
    void pointWorksOnMultipleIterations() {
        System.out.println("Ce test peut prendre quelques secondes");
        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            assertTrue(pointsWorks());
        }
    }

    @Test
    void pointAtWorksWithOnlySingleRoutesInSegments() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2 , p2.distanceTo(p1), Functions.constant(NaN));
        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        MultiRoute m0 = new MultiRoute(List.of(s0));

        PointCh expected1 = origin;
        PointCh actual1 = m0.pointAt(0);
        assertEquals(expected1, actual1);

        PointCh expected2 = origin;
        PointCh actual2 = m0.pointAt( - 100);
        assertEquals(expected2, actual2);

        PointCh expected3 = s0.pointAt(EDGE_LENGTH + 10 );
        PointCh actual3 = m0.pointAt(EDGE_LENGTH + 10);
        assertEquals(expected3, actual3);

        PointCh expected4 = p2;
        PointCh actual4 = m0.pointAt(Double.POSITIVE_INFINITY);
        assertEquals(expected4, actual4);
    }

    @Test
    void pointAtWorksWithMultiRouteAndSingleRoutesInSegments() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        PointCh p3 = new PointCh( p2.e() + EDGE_LENGTH, p2.n() + EDGE_LENGTH);
        PointCh p4 = new PointCh(p3.e(), p3.n() + EDGE_LENGTH + EDGE_LENGTH);
        PointCh p5 = new PointCh(p4.e() + EDGE_LENGTH + EDGE_LENGTH, p2.n());

        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2 , p2.distanceTo(p1), Functions.constant(NaN));
        Edge e2 = new Edge(2, 3, p2, p3, p3.distanceTo(p2), Functions.constant(NaN));
        Edge e3 = new Edge(3, 4, p3, p4, p4.distanceTo(p3), Functions.constant(NaN));
        Edge e4 = new Edge( 4, 5, p4, p5, p5.distanceTo(p4), Functions.constant(NaN));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2, e3));
        SingleRoute s2 = new SingleRoute(List.of(e4));

        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(m0, s1, s2));

        PointCh expected1 = origin;
        PointCh actual1 =  m1.pointAt(-5654);
        assertEquals(expected1, actual1);

        PointCh expected2 = origin;
        PointCh actual2 = m1.pointAt(0);
        assertEquals(expected2, actual2);

        PointCh expected3 = p4;
        PointCh actual3 = m1.pointAt(401 + e2.length());
        assertEquals(expected3, actual3);

        PointCh expected4 = e4.pointAt(550 - 401 - e2.length());
        PointCh actual4 = m1.pointAt(550);
        assertEquals(expected4, actual4);

        PointCh expected5 = p5;
        PointCh actual5 = m1.pointAt(904.243);
        assertEquals(expected5, actual5);

        PointCh expected6 = p5;
        PointCh actual6 = m1.pointAt(POSITIVE_INFINITY);
        assertEquals(expected6, actual6);
    }

    @Test
    void elevationAtWorksWithMultiRoutesAndSingleRoutesInSegments() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        PointCh p3 = new PointCh( p2.e() + EDGE_LENGTH, p2.n() + EDGE_LENGTH);
        PointCh p4 = new PointCh(p3.e(), p3.n() + EDGE_LENGTH + EDGE_LENGTH);
        PointCh p5 = new PointCh(p4.e() + EDGE_LENGTH + EDGE_LENGTH, p2.n());

        List<float[]> randomSamplesList = listOfRandomFloatArrays(4);
        float[] samples0 = randomSamplesList.get(0);
        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin),
                Functions.sampled((samples0) , p1.distanceTo(origin)));
        Edge e1 = new Edge(1, 2, p1, p2 , p2.distanceTo(p1),
                Functions.constant(NaN));
        float[] samples2 = randomSamplesList.get(1);
        Edge e2 = new Edge(2, 3, p2, p3, p3.distanceTo(p2),
                Functions.sampled(samples2, p3.distanceTo(p2)));
        float[] samples3 = randomSamplesList.get(2);
        Edge e3 = new Edge(3, 4, p3, p4, p4.distanceTo(p3),
                Functions.sampled(samples3, p4.distanceTo(p3)));
        float[] samples4 = randomSamplesList.get(3);
        Edge e4 = new Edge( 4, 5, p4, p5, p5.distanceTo(p4),
                Functions.sampled(samples4, p5.distanceTo(p4)));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2, e3));
        SingleRoute s2 = new SingleRoute(List.of(e4));

        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(m0, s1, s2));


        double actual1 = m1.elevationAt(150);
        assertTrue(isNaN(actual1));

        double expected2 = e4.elevationAt(0);
        double actual2 = m1.elevationAt(401 + e2.length());
        assertEquals(expected2, actual2);

        double expected3 = e0.elevationAt(50);
        double actual3 = m1.elevationAt(50);
        assertEquals(expected3, actual3);

        assertEquals(m1.elevationAt(0), m1.elevationAt(-9879));
        assertEquals(m1.elevationAt(m1.length()), m1.elevationAt(8769876));

    }

    private List<float[]> listOfRandomFloatArrays(int numberOfArrays) {
        var rng = newRandom();
        List<float[]> l= new ArrayList<>();
        for (int i = 0; i < numberOfArrays; i++) {
            int arrayLength = rng.nextInt(100);
            float[] arr = new float[arrayLength];
            for (int j = 0; j < arr.length; j++) {
                arr[j] = rng.nextFloat(200);
            }
            l.add(arr);
        }
        return l;
    }

    @Test
    void nodeClosestToWorks() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        PointCh p3 = new PointCh( p2.e() + EDGE_LENGTH, p2.n() + EDGE_LENGTH);
        PointCh p4 = new PointCh(p3.e(), p3.n() + EDGE_LENGTH + EDGE_LENGTH);
        PointCh p5 = new PointCh(p4.e() + EDGE_LENGTH + EDGE_LENGTH, p2.n());

        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2, p2.distanceTo(p1), Functions.constant(NaN));
        Edge e2 = new Edge(2, 3, p2, p3, p3.distanceTo(p2), Functions.constant(NaN));
        Edge e3 = new Edge(3, 4, p3, p4, p4.distanceTo(p3), Functions.constant(NaN));
        Edge e4 = new Edge( 4, 5, p4, p5, p5.distanceTo(p4),Functions.constant(NaN));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2, e3));
        SingleRoute s2 = new SingleRoute(List.of(e4));

        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(m0, s1, s2));

        int expected1 = 0;
        int actual1 = m1.nodeClosestTo(-8679);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = m1.nodeClosestTo(0);
        assertEquals(expected2, actual2);

        int expected3  = 3;
        int actual3 = m1.nodeClosestTo(p1.distanceTo(origin)
                + p2.distanceTo(p1)
                + p3.distanceTo(p2)
        );
        assertEquals(expected3, actual3);

        int expected4 = 5;
        int actual4 = m1.nodeClosestTo(p1.distanceTo(origin)
                + p2.distanceTo(p1)
                + p3.distanceTo(p2)
                + p4.distanceTo(p3)
                + p5.distanceTo(p4)
        );
        assertEquals(expected4, actual4);

        int expected5 = 5;
        int actual5 = m1.nodeClosestTo(45678);
        assertEquals(expected5, actual5);
    }




}
