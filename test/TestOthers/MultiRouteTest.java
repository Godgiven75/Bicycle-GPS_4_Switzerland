package TestOthers;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.MultiRoute;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiRouteTest {
    @Test
    public MultiRoute multiRouteConstructorForTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        return new MultiRoute(listeRoutes);
    }

    @Test
    public void indexOfSegmentAtTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas sup à la length
        int expected0 = 1;
        int actual0 = m.indexOfSegmentAt(SwissBounds.MIN_E + 25);
        assertEquals(expected0, actual0);
        //Cas position négative
        int expected1 = 0;
        int actual1 = m.indexOfSegmentAt(-25);
        assertEquals(expected1, actual1);
        //Cas position nulle
        int expected2 = expected1;
        int actual2 = m.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);
        //Cas position = length
        int expected3 = expected0;
        int actual3 = m.indexOfSegmentAt(25);
        assertEquals(expected3, actual3);
        //Cas normal
        int expected4 = 1;
        int actual4 = m.indexOfSegmentAt(25);
        assertEquals(expected4, actual4);
    }

    @Test
    public void lengthTest(){
        MultiRoute m = multiRouteConstructorForTest();
        double expected0 = 35.0;
        double actual0 = m.length();
        assertEquals(expected0, actual0);
    }

    @Test
    public void edgesTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Edge> allEdges = new ArrayList<>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        MultiRoute m = new MultiRoute(listeRoutes);

        allEdges.add(edge0);
        allEdges.add(edge1);
        allEdges.add(edge2);
        allEdges.add(edge3);
        List<Edge> expected0 = allEdges;
        List<Edge> actual0 = m.edges();
        assertEquals(expected0, actual0);
    }

    @Test
    public void pointsTest(){
        MultiRoute m = multiRouteConstructorForTest();
        List<PointCh> allPoints = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);

        allPoints.add(fromPoint0);
        allPoints.add(fromPoint1);
        allPoints.add(fromPoint2);
        allPoints.add(fromPoint3);
        allPoints.add(toPoint3);

        List<PointCh> expected0 = allPoints;
        List<PointCh> actual0 = m.points();
        assertEquals(expected0, actual0);
    }

    @Test
    public void pointAtTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas normal
        PointCh expected0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh actual0 = m.pointAt(5);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        PointCh expected1 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual1 = m.pointAt(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        PointCh expected2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh actual2 = m.pointAt(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        PointCh expected3 = expected2;
        PointCh actual3 = m.pointAt(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        PointCh expected4 = expected1;
        PointCh actual4 = m.pointAt(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        PointCh expected5 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh actual5 = m.pointAt(20.0);
        assertEquals(expected5, actual5);
    }

    @Test
    public void elevationAtTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        MultiRoute m = new MultiRoute(listeRoutes);

        //Cas normal
        double expected0 = edge1.elevationAt(3);
        double actual0 = m.elevationAt(8);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        double expected1 = edge3.elevationAt(15);
        double actual1 = m.elevationAt(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        double expected2 = edge0.elevationAt(0);
        double actual2 = m.elevationAt(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        double expected3 = expected2;
        double actual3 = m.elevationAt(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        double expected4 = expected1;
        double actual4 = m.elevationAt(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        double expected5 = edge2.elevationAt(7);
        double actual5 = m.elevationAt(17);
        assertEquals(expected5, actual5);
    }

    @Test
    public void nodeClosestToTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas normal
        int expected0 = 3;
        int actual0 = m.nodeClosestTo(25);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        int expected1 = 4;
        int actual1 = m.nodeClosestTo(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        int expected2 = 0;
        int actual2 = m.nodeClosestTo(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        int expected3 = expected2;
        int actual3 = m.nodeClosestTo(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        int expected4 = expected1;
        int actual4 = m.nodeClosestTo(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        int expected5 = 2;
        int actual5 = m.nodeClosestTo(12);
        assertEquals(expected5, actual5);
    }

    @Test
    public void pointClosestToTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //itinéraire horizontal
        //Cas normal
        PointCh point0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N + 1);
        PointCh expected0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh actual0 = m.pointClosestTo(point0).point();
        assertEquals(expected0, actual0);
        //Cas point.e() sup à length
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 40);
        PointCh expected1 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual1 = m.pointClosestTo(point1).point();
        assertEquals(expected1, actual1);
        //Cas point en dessus du premier de l'itinéraire
        PointCh point2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 40);
        PointCh expected2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh actual2 = m.pointClosestTo(point2).point();
        assertEquals(expected2, actual2);
        //Cas point en dessus du dernier de l'itinéraire
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N + 40);
        PointCh expected3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual3 = m.pointClosestTo(point3).point();
        //Ne devrait pas passer: GOOD
        //assertEquals(expected3, actual3);
        //Cas normal 2.0
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 22, SwissBounds.MIN_N + 40);
        PointCh expected4 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh actual4 = m.pointClosestTo(point4).point();
        //Ne devrait pas passer: GOOD
        //assertEquals(expected4, actual4);
        //Cas point sur l'itinéraire
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh expected5 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh actual5 = m.pointClosestTo(point5).point();
        assertEquals(expected5, actual5);
    }

    @Test
    public void pointClosestToTest2()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(10, 0), 10, null),
                                new Edge(4, 5, pointCreator(10, 0), pointCreator(20, 0), 10, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(20, 0), pointCreator(30, 0), 10, null),
                                new Edge(6, 7, pointCreator(30, 0), pointCreator(40, 0), 10, null))
                )
                )
        )));
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i + SwissBounds.MIN_E, mr1.pointClosestTo(pointCreator(i, 1)).point().e());
        }
    }

    private PointCh pointCreator(int e, int n)
    {
        return new PointCh(SwissBounds.MIN_E + e, SwissBounds.MIN_N + n);
    }
}
