package ch.epfl.javelo.projection.data;

import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorsTest {


    @Test
    public void sectorInAreaNormalCase() {
        byte[] testbuffer = new byte[6*16_384];
        for (int i = 0; i < 16_384*6; i++) {
            testbuffer[i] = 0;
        }
        testbuffer[3] = 1;
        testbuffer[5] = 1;
        testbuffer[9] = 1;
        testbuffer[11] = 1;
        testbuffer[15] = 1;
        testbuffer[17] = 1;
        testbuffer[128*6 + 3] = 1;
        testbuffer[128*6 + 5] = 1;
        testbuffer[128*6 + 9] = 1;
        testbuffer[128*6 + 11] = 1;
        testbuffer[128*6 + 15] = 1;
        testbuffer[128*6 + 17] = 1;
        testbuffer[256*6 + 3] = 1;
        testbuffer[256*6 + 5] = 1;
        testbuffer[256*6 + 9] = 1;
        testbuffer[256*6 + 11] = 1;
        testbuffer[256*6 + 15] = 1;
        testbuffer[256*6 + 17] = 1;
        System.out.println(testbuffer.length);
        GraphSectors buffer = new GraphSectors(ByteBuffer.wrap(testbuffer));
        List<GraphSectors.Sector> temoin = new ArrayList<>();
        temoin.add(0, new GraphSectors.Sector(1,2));
        temoin.add(1, new GraphSectors.Sector(1,2));
        temoin.add(2, new GraphSectors.Sector(1,2));
        temoin.add(3, new GraphSectors.Sector(1,2));
        temoin.add(4, new GraphSectors.Sector(1,2));
        temoin.add(5, new GraphSectors.Sector(1,2));
        temoin.add(6, new GraphSectors.Sector(1,2));
        temoin.add(7, new GraphSectors.Sector(1,2));
        temoin.add(8, new GraphSectors.Sector(1,2));
        System.out.println("TemoinArraySize: " + temoin.size());
        List<GraphSectors.Sector> test = buffer.sectorsInArea(new PointCh(2_485_000 + 1.5 * 349_000.0/128.0,
                1_075_000 + 1.5 * 221_000.0/128.0 ), 2000);
        assertArrayEquals(temoin.toArray(), test.toArray());
    }

    @Test
    public void sectorInAreaBottomLeft() {
        byte[] testbuffer = new byte[6*16_384];
        for (int i = 0; i < 16_384*6; i++) {
            testbuffer[i] = 0;
        }
        testbuffer[3] = 1;
        testbuffer[5] = 2;
        testbuffer[9] = 1;
        testbuffer[11] = 1;
        testbuffer[128*6 + 3] = 1;
        testbuffer[128*6 + 5] = 1;
        testbuffer[128*6 + 9] = 2;
        testbuffer[128*6 + 11] = 1;
        System.out.println(testbuffer.length);
        GraphSectors buffer = new GraphSectors(ByteBuffer.wrap(testbuffer));
        List<GraphSectors.Sector> temoin = new ArrayList<>();
        temoin.add(0, new GraphSectors.Sector(1,3));
        temoin.add(1, new GraphSectors.Sector(1,2));
        temoin.add(2, new GraphSectors.Sector(1,2));
        temoin.add(3, new GraphSectors.Sector(2,3));
        System.out.println("TemoinArraySize: " + temoin.size());
        List<GraphSectors.Sector> test = buffer.sectorsInArea(new PointCh(2_485_000, 1_075_000), 3000);
        assertArrayEquals(temoin.toArray(), test.toArray());
    }

}
