package TestOthers;

import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void ErrorIfFileNotFound(){
        assertThrows(NoSuchFileException.class, () -> Graph.loadFrom(Path.of("eazeza")));
        assertThrows(NoSuchFileException.class, () -> Graph.loadFrom(Path.of("eajsza")));
    }

    @Test
    void NoErrorIfFileFound() {
        assertDoesNotThrow(() -> Graph.loadFrom(Path.of("lausanne"))); }

    @Test
    void nodeCountWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
//            (short) 0x180C, (short) 0xFEFF,
//            (short) 0xFFFE, (short) 0xF000 //TypeIn3
                (short) 0x180C, (short) 0x1212 //Type2
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 1;
        int actual0 = graph.nodeCount();
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 2;
        int actual1 = graph1.nodeCount();
        assertEquals(expected1, actual1);
    }

    @Test
    void nodePointWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        PointCh expected0 = new PointCh(2_600_000, 1_200_000);
        PointCh actual0 = graph.nodePoint(0);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        PointCh expected1 = new PointCh(2_536_263, 1_215_736);
        PointCh actual1 = graph1.nodePoint(0);
        assertEquals(expected1, actual1);
    }

    @Test
    void nodeOutDegreeWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 2;
        int actual0 = graph.nodeOutDegree(0);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 1;
        int actual1 = graph1.nodeOutDegree(1);
        assertEquals(expected1, actual1);
    }

    @Test
    void nodeOutEdgeIdWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 9;
        int actual0 = graph.nodeOutEdgeId(0, 2);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 31 + 5;
        int actual1 = graph1.nodeOutEdgeId(1, 5);
        assertEquals(expected1, actual1);
    }

    //A FAIRE!
    @Test
    void nodeClosestToWorksOnGivenValue() throws IOException {

    }

    @Test
    void NodeClosedToMINUSONE() throws IOException {
        assertEquals(-1, Graph.loadFrom(Path.of("lausanne")).nodeClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 0));
    }

    @Test
    void NodeClosedTo0() throws IOException {
        assertEquals(194878, Graph.loadFrom(Path.of("lausanne")).nodeClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10000000));
    }

    @Test
    void NodeClosedToMAX() throws IOException {
        assertEquals(26, Graph.loadFrom(Path.of("lausanne")).nodeClosestTo(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 10000000));
    }

    @Test
    void edgeTargetNodeIdWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 12;
        int actual0 = graph.edgeTargetNodeId(0);
        assertEquals(expected0, actual0);
    }

    @Test
    void edgeIsInvertedWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        boolean expected0 = true;
        boolean actual0 = graph.edgeIsInverted(0);
        assertEquals(expected0, actual0);
    }

    //ASSISTANT HELP!
    @Test
    void edgeAttributesWorksOnGivenValue() {}

    @Test
    void edgeLengthWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        double expected0 = 16.6875;
        double actual0 = graph.edgeLength(0);
        assertEquals(expected0, actual0);
    }

    @Test
    void edgeElevationGainWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        double expected0 = 16.0;
        double actual0 = graph.edgeElevationGain(0);
        assertEquals(expected0, actual0);
    }

    //LINA HELP!
    @Test
    void edgeProfileWorksOnGivenValue() {}

    @Test
    void ThrowElevationProfile()
    {
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-1, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{1}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{}));
    }

    @Test
    void TestWithCalculationsActualFunction()
    {
        float[] values = new float[]{5,7,0,2,10};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(0, ep.minElevation());
        assertEquals(10, ep.maxElevation());
        assertEquals(12, ep.totalAscent());
        assertEquals(7, ep.totalDescent());
        assertEquals(6, ep.elevationAt(2.5));
        assertEquals(7, ep.elevationAt(5));
        assertEquals(1, ep.elevationAt(12.5));
        assertEquals(10, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(0, ep.elevationAt(10));
    }

    @Test
    void TestWithCalculationsCst()
    {
        float[] values = new float[]{5,5};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(5, ep.minElevation());
        assertEquals(5, ep.maxElevation());
        assertEquals(0, ep.totalAscent());
        assertEquals(0, ep.totalDescent());
        assertEquals(5, ep.elevationAt(2.5));
        assertEquals(5, ep.elevationAt(5));
        assertEquals(5, ep.elevationAt(12.5));
        assertEquals(5, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(5, ep.elevationAt(10));
    }
}

