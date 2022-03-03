package ch.epfl.javelo.projection.data;

import ch.epfl.javelo.data.GraphNodes;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphNodesTest {

    @Test
    public void returnsTheRightCountOfNodesInBuffer() {
        GraphNodes g = new GraphNodes(IntBuffer.wrap(new int[]{1, 2, 3}));
        int expected = 1;
        assertEquals(expected, g.count());
    }

    @Test
    public void returnsTheRightECoordinate() {
        GraphNodes g = new GraphNodes(IntBuffer.wrap(new int[]{1, 2, 3}));
        int expected = 1;
        assertEquals(expected, g.nodeE(0));
    }

    @Test
    public void returnsTheRightNCoordinate() {
        GraphNodes g = new GraphNodes(IntBuffer.wrap(new int[]{1, 2, 3}));
        int expected = 2;
        assertEquals(expected, g.nodeN(0));
    }

    @Test
    public void returnsTheNbOfLeavingEdges() {
        GraphNodes g = new GraphNodes(IntBuffer.wrap(new int[]{1, 2, 0b01000000000000000000000000000000}));
        int expected = 4;
        assertEquals(expected, g.outDegree(0));
    }

    @Test
    public void returnsTheRightIdentityOfTheIthEdge() {
        GraphNodes g = new GraphNodes(IntBuffer.wrap(new int[]{1, 2, 0b01100000000000000011000000000000}));
        System.out.println(0b11000000000000);
        int expected = 12292;
        assertEquals(expected, g.edgeId(0, 5));
    }
}
