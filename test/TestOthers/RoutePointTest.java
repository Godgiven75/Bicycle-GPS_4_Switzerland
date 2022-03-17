package TestOthers;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutePointTest {

    @Test
    void withPositionShiftedByWorksOnGivenValue(){
        //Shift by 2000
        RoutePoint point0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual0 = point0.withPositionShiftedBy(2000);
        RoutePoint expected0 = new RoutePoint(new PointCh(2800000, 1080000), 14839, 1000);
        assertEquals(expected0, actual0);

        //Shift by -2000
        RoutePoint point1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = point1.withPositionShiftedBy(-2000);
        RoutePoint expected1 = new RoutePoint(new PointCh(2800000, 1080000), 10839, 1000);
        assertEquals(expected1, actual1);

        //Shift by 0
        RoutePoint point2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = point2.withPositionShiftedBy(0);
        RoutePoint expected2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        assertEquals(expected2, actual2);
    }

    @Test
    void minWorksOnGivenValue(){
        //That plus petit
        RoutePoint pointThis0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint pointThat0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint actual0 = pointThis0.min(pointThat0);
        RoutePoint expected0 = pointThat0;
        assertEquals(expected0, actual0);

        //Egalité
        RoutePoint pointThis1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint pointThat1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = pointThis1.min(pointThat1);
        RoutePoint expected1 = pointThis1;
        assertEquals(expected1, actual1);

        //This plus petit
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint pointThat2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = pointThis2.min(pointThat2);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }

    @Test
    void min2WorksOnGivenValue(){
        //ThatDistanceToReference plus petit
        RoutePoint pointThis0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual0 = pointThis0.min(new PointCh(2800000, 1080000), 12839, 999);
        RoutePoint expected0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 999);
        assertEquals(expected0, actual0);

        //Egalité
        RoutePoint pointThis1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = pointThis1.min(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint expected1 = pointThis1;
        assertEquals(expected1, actual1);

        //This plus petit
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint actual2 = pointThis2.min(new PointCh(2800000, 1080000), 12839, 5002);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }

    @Test
    void withPositionShiftedBybasicTest()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 100);
        RoutePoint result = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 100);

        RoutePoint shifted = rp.withPositionShiftedBy(100);
        assertEquals(result.point().e(), shifted.point().e());
        assertEquals(result.point().n(), shifted.point().n());
        assertEquals(result.distanceToReference(), shifted.distanceToReference());
        assertEquals(result.position(), shifted.position());
    }

    @Test
    void MinTests()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 50);
        RoutePoint result = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 10);

        RoutePoint found = rp.min(result);
        assertEquals(result.point().e(), found.point().e());
        assertEquals(result.point().n(), found.point().n());
        assertEquals(result.distanceToReference(), found.distanceToReference());
        assertEquals(result.position(), found.position());
    }

    @Test
    void MinTestsNotCreated()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 50);

        RoutePoint found = rp.min(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 100);
        assertEquals(rp.point().e(), found.point().e());
        assertEquals(rp.point().n(), found.point().n());
        assertEquals(rp.distanceToReference(), found.distanceToReference());
        assertEquals(rp.position(), found.position());
    }

    @Test
    void MinTestsCreated()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 50);
        RoutePoint result = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 10);

        RoutePoint found = rp.min(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 10);
        assertEquals(result.point().e(), found.point().e());
        assertEquals(result.point().n(), found.point().n());
        assertEquals(result.distanceToReference(), found.distanceToReference());
        assertEquals(result.position(), found.position());
    }
}
