package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutePointTest {

    @Test
    public void withPositionShiftedByReturnsShiftedPointPosition() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1800, 3000);
        assertEquals(test, base.withPositionShiftedBy(300));
        RoutePoint base2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1200, 3000);
        assertEquals(test2, base2.withPositionShiftedBy(-300));
    }

    @Test
    public void minReturnsThisIfItsDistanceToReferenceIsBelowOrEqualToThat() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_093), 1800, 3000);
        assertEquals(base, base.min(test));
        RoutePoint base2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 2999.99);
        RoutePoint test2 = new RoutePoint(new PointCh(2_485_000, 1_075_030), 1800, 3000);
        assertEquals(base, base.min(test));
    }

    @Test
    public void minReturnsThatifThisDistanceToRefIsGreater() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_023), 1800, 2978);
        assertEquals(test, base.min(test));
    }

    @Test
    public void minReturnsThisIfDistanceToRefIsBelowOrEqualToThatDistanceToRef() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        assertEquals(base, base.min(new PointCh(2_485_000, 1_075_023), 1800, 3000));
        RoutePoint base2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 2745);
        assertEquals(base2, base2.min(new PointCh(2_485_000, 1_075_023), 1800, 3000));
    }

    @Test
    public void minReturnsNewInstanceOfRoutePointWithGivenArgumentsIfThisDistanceToRefIsGreater() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_023), 1800, 2999.984029348);
        assertEquals(test, base.min(new PointCh(2_485_000, 1_075_023), 1800, 2999.984029348));
    }
}
