package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiRouteTest {

    @Test
    void indexOfSegmentAtWorksWithOnlySimpleRoutes() {
        Edge e = new Edge(0, 1,null ,null, 1000, Functions.constant(NaN));
        SingleRoute sr0 = new SingleRoute(List.of(e));
        SingleRoute sr1 = new SingleRoute(List.of(e));
        SingleRoute sr2 = new SingleRoute(List.of(e));
        MultiRoute mr = new MultiRoute(List.of(sr0, sr1, sr2));
        int actual1 = mr.indexOfSegmentAt(-500);
        int expected1 = 0;
        assertEquals(expected1, actual1);

        int actual2 = mr.indexOfSegmentAt(100);
        int expected2 = 0;
        assertEquals(expected2,actual2);

        int actual3 = mr.indexOfSegmentAt(1500);
        int expected3 = 1;
        assertEquals(expected3, actual3);

        int actual4 = mr.indexOfSegmentAt(3000);
        int expected4 = 2;
        assertEquals(expected4, actual4);

        int actual5 = mr.indexOfSegmentAt(4500);
        int expected5 = 2;
        assertEquals(expected5, actual5);
    }

    @Test
    void indexOfSegmentAtWorksWithMultiRoutesAndSingleRoutes1() {
        Edge e = new Edge(0, 1,null ,null, 1000, Functions.constant(NaN));
        SingleRoute sr0 = new SingleRoute(List.of(e));
        SingleRoute sr1 = new SingleRoute(List.of(e));
        SingleRoute sr2 = new SingleRoute(List.of(e));
        MultiRoute mr1 = new MultiRoute(List.of(sr0, sr1, sr2));
        MultiRoute mr2 = new MultiRoute(List.of(sr0, sr1, sr2));
        MultiRoute m = new MultiRoute(List.of(mr1, mr2));

        int expected1 = 0;
        int actual1 = m.indexOfSegmentAt(-1000);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = m.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);

        int expected3 = 0;
        int actual3 = m.indexOfSegmentAt(750);
        assertEquals(expected3, actual3);




    }

}
