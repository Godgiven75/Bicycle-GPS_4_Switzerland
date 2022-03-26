package TestOthers;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleRouteTest {

    private List<Edge> edges = new ArrayList<>();
    private List<PointCh> points = new ArrayList<>();
    SingleRoute route = route();

    private SingleRoute route() {
        float[] samples1 = new float[]{200, 100, 150, 250,300};
        DoubleUnaryOperator profile1 = Functions.sampled(samples1, 100 * sqrt(2));
        Edge edge1 = new Edge(0, 4,
                new PointCh(2500100, 1100100), new PointCh(2500200, 1100200),
                100 * sqrt(2), profile1);
        points.add(new PointCh(2500100, 1100100));

        float[] samples2 = new float[]{0, 200, 100, 400,0};
        PointCh from2 = new PointCh(2500200, 1100200);
        PointCh to2 = new PointCh(2500000, 1100000);
        DoubleUnaryOperator profile2 = Functions.sampled(samples2, from2.distanceTo(to2));
        Edge edge2 = new Edge(4, 7,
                from2, to2, from2.distanceTo(to2), profile2);
        points.add(from2);
        points.add(to2);

        float[] samples3 = new float[]{300, 100, 200, 100, 200};
        PointCh from3 = new PointCh(2500000, 1100000);
        PointCh to3 = new PointCh(2500400, 1100400);
        DoubleUnaryOperator profile3 = Functions.sampled(samples3, from3.distanceTo(to3));
        Edge edge3 = new Edge(7, 5,
                from3, to3, from3.distanceTo(to3), profile3);
        points.add(to3);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        SingleRoute route1 = new SingleRoute(edges);
        return route1;
    }
    /*@Test
    void pointAtTest() {
        // can the position exceed the length? what to do in that case
        PointCh expected = new PointCh(2500500, 1100500);
        PointCh actual = route.pointAt(800 * sqrt(2));
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500000, 1100000);
        actual = route.pointAt(-100 * sqrt(2));
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500700, 1100700);
        actual = route.pointAt(1000 * sqrt(2));
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());
    }*/

    // 3e test
    /*@Test
    void pointAtWorksWithPointsBeyondRouteMiMi() {
        PointCh pointBeyondRoute = ourRoute().pointAt(ourRoute().length()+45);
        PointCh pointBeforeRoute = ourRoute().pointAt(edgeBeforeRoute.length()/2d);
        System.out.println(p0.distanceTo(p1));
        System.out.println(edgeBeforeRoute.length()/2d);
        assertEquals(p3, pointBeyondRoute, "oh no…our code…it's broken"); // a point right after the route
        assertEquals(p1, pointBeforeRoute, "oh no…our code…it's broken"); // a point right before the route
    }*/
    PointCh p0 = new PointCh(2600100, 1200400);
    PointCh p1 = new PointCh(2600123, 1200456);
    PointCh p2 = new PointCh(2600456, 1200789);
    PointCh p3 = new PointCh(2600789, 1200123);
    PointCh p4 = new PointCh(2601000, 1201000);

    Edge myEdge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
    Edge myEdge2 = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Math.sin(x));
    Edge edgeBeforeRoute  =  new Edge(3, 4, p0, p1, p0.distanceTo(p1), x -> Double.NaN);

    public SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(myEdge1);
        edges.add(myEdge2);

        return new SingleRoute(edges);
    }

    // LES EDGES DE CES TEST NE SONT PAS COLLE!! :/
    @Test
    public void indexOfSegmentAt(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges1 =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute singleRoute = new SingleRoute(edges);
        int expected = 0;
        int actual0 = singleRoute.indexOfSegmentAt(4.5);
        //TEST 1:
        assertEquals(expected, actual0);
        int actual1 = singleRoute.indexOfSegmentAt(16);
        //TEST 2:
        assertEquals(expected, actual1);
    }

    @Test
    public void lengthWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        double expected0 = 16.6875;
        double actual0 = singleRoute0.length();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        double expected1 = 21.6875;
        double actual1 = singleRoute1.length();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void edgesWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        List<Edge> actual0 = singleRoute0.edges();
        //TEST 1:
        assertEquals(edges0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        List<Edge> actual1 = singleRoute1.edges();
        //TEST 2:
        assertEquals(edges1, actual1);
    }

    @Test
    public void pointsWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        List<PointCh> expected0 = new ArrayList<>();
        expected0.add(edge0.fromPoint());
        expected0.add(edge0.toPoint());
        List<PointCh> actual0 = singleRoute0.points();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        List<PointCh> expected1 = new ArrayList<>();
        expected1.add(edge0.fromPoint());
        expected1.add(edge1.fromPoint());
        expected1.add(edge1.toPoint());
        List<PointCh> actual1 = singleRoute1.points();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void pointAtWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        PointCh expected0 = edge0.pointAt(4);
        PointCh actual0 = singleRoute0.pointAt(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        PointCh expected1 = edge1.pointAt(4);
        PointCh actual1 = singleRoute1.pointAt(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        PointCh expected2 = edge1.pointAt(5);
        PointCh actual2 = singleRoute1.pointAt(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        PointCh expected3 = edge0.pointAt(0);
        PointCh actual3 = singleRoute1.pointAt(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void elevationAtWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        //double expected0 = edge0.elevationAt(4);
        //double actual0 = singleRoute0.elevationAt(4);
        //TEST 1:
        //assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        //double expected1 = edge1.elevationAt(4);
        //double actual1 = singleRoute1.elevationAt(20.6875);
        //TEST 2:
        //assertEquals(expected1, actual1);
        double expected2 = edge1.elevationAt(5);
        double actual2 = singleRoute1.elevationAt(21.6875);
        //TEST 3:
        System.out.println();
        assertEquals(expected2, actual2);
        double expected3 = edge0.elevationAt(0);
        double actual3 = singleRoute1.elevationAt(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void nodeClosestToWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        int expected0 = edge0.fromNodeId();
        int actual0 = singleRoute0.nodeClosestTo(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        int expected1 = edge1.toNodeId();
        int actual1 = singleRoute1.nodeClosestTo(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        int expected2 = edge1.toNodeId();
        int actual2 = singleRoute1.nodeClosestTo(21.6875);
        //TEST 3:
        //assertEquals(expected2, actual2);
        int expected3 = edge0.fromNodeId();
        int actual3 = singleRoute1.nodeClosestTo(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    /*
    @Test
    public void binarySearchIndexWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute singleRoute = new SingleRoute(edges);
        double position = 100000000;
        //TEST 1: Should return 16.6875
        assertEquals(16.6875, singleRoute.binarySearchIndex(position));
        double position1 = -100000000;
        //TEST 2: Should return 0.0
        assertEquals(0.0, singleRoute.binarySearchIndex(position1));
    }

     */
}
