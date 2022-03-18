package TestOthers;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {

    @Test
    public void EdgeIsFine(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_842_000 << 4,
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


        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);

        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        PointCh pointToTest = new PointCh(2600000, 1085000);

        System.out.println(edge1.positionClosestTo(pointToTest));
    }


    @Test
    void HorizontalEdgeWorksCorrectlyForLimits(){
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);
        DoubleUnaryOperator squared = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return Math.pow(operand, 2);
            }
        };
        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, squared);
        PointCh pointToTest = new PointCh(2600000, 1085000);

        assertEquals(0,edge1.positionClosestTo(fromPoint));
        assertEquals(10, edge1.positionClosestTo(toPoint));


        assertEquals(fromPoint, edge1.pointAt(0));
        assertEquals(toPoint, edge1.pointAt(10));
        assertEquals(new PointCh(2485015, 1076000), edge1.pointAt(5));

        assertEquals(9,edge1.elevationAt(3));
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        assertEquals(384.75f,edge2.elevationAt(0));

    }


}
