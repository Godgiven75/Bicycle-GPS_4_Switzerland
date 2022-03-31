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
import static java.lang.Float.NaN;
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
        System.out.println(delta);
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
        RandomGenerator rng = new Random();
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
    void pointAtWorksSimplestCase() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        Edge e0 = new Edge(0, 1, origin, p1, EDGE_LENGTH, Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2 , EDGE_LENGTH, Functions.constant(NaN));
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




}
