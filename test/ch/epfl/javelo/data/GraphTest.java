package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphTest {

    @Test
    public void loadFromThrowsExceptionIfFileDoesNotExist() throws IOException {
        Path basePath = Path.of("geneve");
        assertThrows(IOException.class, () ->
        {Graph.loadFrom(basePath);} );
    }

    @Test
    public void loadFromGiveTheRightGraph() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(2022));
    }

    @Test
    public void nodesCountWorksOnKnownValues() {

        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        Graph g = new Graph(ns, null, null, null);
        assertEquals(1, g.nodeCount());

        for (int count = 0; count < 100; count += 1) {
            var buffer = IntBuffer.allocate(3 * count);
            var graphNodes = new GraphNodes(buffer);
            g = new Graph(graphNodes, null, null, null);
            assertEquals(count, g.nodeCount());
        }
    }

    @Test
    public void nodePointReturnsNodeIdPosition() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);
        System.out.println(WebMercator.x(Math.toRadians(6.6013034)) + " " + WebMercator.y(Math.toRadians(46.6326106)));
        assertEquals(
                (new PointWebMercator(WebMercator.x(Math.toRadians(6.6013034)), WebMercator.y(Math.toRadians(46.6326106))).toPointCh()),
                g.nodePoint(2022)
        );
    }


    @Test
    public void nodeOutDegreesWorksOnKnownValues() {
        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);

            Graph g = new Graph(graphNodes, null, null, null);
            assertEquals(outDegree, g.nodeOutDegree(nodeId));
        }
    }

    @Test
    public void nodeOutEdgeIdWorks () {
        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            Graph g = new Graph(graphNodes, null, null, null);
            for (int i = 0; i < outDegree; i += 1)
                assertEquals(firstEdgeId + i, g.nodeOutEdgeId(nodeId, i));
        }
    }

    @Test
    public void nodeClosestToWorks() throws IOException {

        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);
        double x = 0.518275214444;
        double y = 0.353664894749;
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        PointWebMercator pwm = new PointWebMercator(x, y);
        PointCh napoleon = pwm.toPointCh();

        int javeloNode = g.nodeClosestTo(napoleon, 0 );
        long expected  = 417273475;
        System.out.println(javeloNode);
        long actual = osmIdBuffer.get(javeloNode);
        assertEquals(expected, actual);
    }

    public static void main(String[] args) throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(153713));

    }

}


