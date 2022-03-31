package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.routing.RoutePoint.NONE;


/**
 * Représente un itinéraire multiple, c.-à-d. composé d'une séquence d'itinéraires contigus
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;

    /**
     * Construit un itinéraire multiple composé des segments donnés, ou lève IllegalArgumentException si la liste des segments est vide
     * @param segments
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }


    @Override
    public int indexOfSegmentAt(double position) {
        int tempPosition = 0;
        int index = 0;
        for (Route r : segments) {
            tempPosition += r.length();
            if (position <= tempPosition)
                return index + r.indexOfSegmentAt(position);
            index += 1 + r.indexOfSegmentAt(position);
        }
        return indexOfSegmentAt(tempPosition);
    }

    @Override
    public double length() {
        double totalLength = 0;
        for (Route r : segments) {
            totalLength += r.length();
        }
        return totalLength;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> l = new ArrayList<>();
        for (Route r : segments) {
            l.addAll(r.edges());
        }
        return l;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> l = new ArrayList<>();
        for (Route r : segments) {
            l.addAll(r.points());
        }
        return l;
    }
    private int findRouteIndex(double position) {
        int tempPosition = 0;
        int index = 0;
        for (Route r : segments) {
            tempPosition += r.length();
            if (tempPosition >= position)
                return index;
            ++index;
        }
        return segments.size() - 1;
    }

    private double positionOnRoute(double position) {
        int index = findRouteIndex(position);
        double lengthOfPreviousRoutes = 0;
        for (int i = 0; i < index; i++) {
            lengthOfPreviousRoutes += segments.get(i).length();
        }
        return position - lengthOfPreviousRoutes;
    }
    @Override
    public PointCh pointAt(double position) {
        int routeIndex = findRouteIndex(position);
        Route r = segments.get(routeIndex);
        return r.pointAt(positionOnRoute(position)); // il faut sans doute ajuster la position pour avoir la position sur s...
    }

    @Override
    public double elevationAt(double position) {
        int routeIndex = findRouteIndex(position);
        Route r = segments.get(routeIndex);
        return r.elevationAt(positionOnRoute(position));
    }

    @Override
    public int nodeClosestTo(double position) {
        int routeIndex = findRouteIndex(position);
        Route r = segments.get(routeIndex);
        return r.nodeClosestTo(positionOnRoute(position));
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = NONE;
        for (Route r : segments) {
            RoutePoint closestPointOnRoute = r.pointClosestTo(point);
            closestPoint = closestPoint.min(closestPointOnRoute);
        }
        return closestPoint;
    }
}
