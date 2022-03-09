package ch.epfl.javelo.projection.data;

import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorsTest {


    @Test
    public void sectorInAreaNormalCase() {
        byte[] testbuffer = new byte[6*16_384];
        testbuffer[6*129+4] = 0b00000001;
        testbuffer[6*129+6] = 0b00000010;
        testbuffer[6*130+4] = 0b00000010;
        testbuffer[6*130+6] = 0b00000011;
        testbuffer[6*131+4] = 0b00000011;
        testbuffer[6*131+6] = 0b00000100;
        testbuffer[6*257+4] = 0b00000100; // 4
        testbuffer[6*257+6] = 0b00000101;
        testbuffer[6*258+4] = 0b00000101;
        testbuffer[6*258+6] = 0b00000110;
        testbuffer[6*259+4] = 0b00000110;
        testbuffer[6*259+6] = 0b00000111;
        GraphSectors buffer = new GraphSectors(ByteBuffer.wrap(testbuffer));
        List<GraphSectors.Sector> temoin = new ArrayList<>(16384);
        while (temoin.size() < 16385)
        {
            temoin.add(null);
        }
        temoin.set(129, new GraphSectors.Sector(1,2));
        temoin.set(130, new GraphSectors.Sector(2,3));
        temoin.set(131, new GraphSectors.Sector(3,4));
        temoin.set(257, new GraphSectors.Sector(4,5));
        temoin.set(258, new GraphSectors.Sector(5,6));
        temoin.set(259, new GraphSectors.Sector(6,7));
        System.out.println(temoin.size());
        List<GraphSectors.Sector> test = buffer.sectorsInArea(new PointCh(2_491_816,1_079_316), 3_408);
        assertEquals(temoin, test);
    }

}
