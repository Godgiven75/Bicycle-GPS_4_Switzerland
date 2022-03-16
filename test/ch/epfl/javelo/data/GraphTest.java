package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphTest {

    @Test
    public void loadFromThrowsExceptionIfFileDoesNotExist() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph.loadFrom(basePath);
        assertThrows(IOException.class, () ->
            {FileChannel channel =  FileChannel.open(basePath);} );
    }

    @Test
    public void loadFromGiveTheRightGraph() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);
    }

    @Test

}
