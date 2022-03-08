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
        GraphSectors buffer = new GraphSectors(ByteBuffer.wrap(new byte[]{1, 2, 3}));
        List<GraphSectors.Sector> temoin = new ArrayList<GraphSectors.Sector>();
        temoin.add(temoin.set(129, new GraphSectors.Sector(1,2)));
        temoin.add(temoin.set(130, new GraphSectors.Sector(2,3)));
        temoin.add(temoin.set(131, new GraphSectors.Sector(3,4)));
        temoin.add(temoin.set(257, new GraphSectors.Sector(4,5)));
        temoin.add(temoin.set(258, new GraphSectors.Sector(5,6)));
        temoin.add(temoin.set(259, new GraphSectors.Sector(6,7)));

        List<GraphSectors.Sector> test = buffer.sectorsInArea(new PointCh(2_491_816,1_079_316), 3_408);
        assertEquals(test, )
    }

}
