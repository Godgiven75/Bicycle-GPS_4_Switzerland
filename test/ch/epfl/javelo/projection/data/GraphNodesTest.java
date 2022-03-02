package ch.epfl.javelo.projection.data;

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
}
