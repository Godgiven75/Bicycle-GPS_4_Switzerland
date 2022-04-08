package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.routing.RoutePoint.NONE;

/**
 * Représente un itinéraire multiple, c.-à-d. composé d'une séquence
 * d'itinéraires contigus.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;

    /**
     * Construit un itinéraire multiple composé des segments donnés.
     *
     * @param segments les segments de l'itinéraire
     * @throws IllegalArgumentException si la liste des segments est vide
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }


    @Override
    public int indexOfSegmentAt(double position) {
        int tempPosition = 0;
        int index = 0;
        double positionOnItinerary = Math2.clamp(0, position, length());
        for (Route r : segments) {
            tempPosition += r.length();
            if (positionOnItinerary <= tempPosition)
                return index + r.indexOfSegmentAt(positionOnItinerary);
            index += 1 + r.indexOfSegmentAt(position - tempPosition);
        }
        return segments.size() - 1;
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
        Route r0 = segments.get(0);
        l.add(r0.points().get(0));
        for (Route r : segments) {
            List<PointCh> rPoints = r.points();
            for (int i = 1; i < rPoints.size(); i++) {
                l.add(rPoints.get(i));
            }
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
        return r.pointAt(positionOnRoute(position));
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
        double tempPosition = 0;
        for (Route r : segments) {
            RoutePoint closestPointOnRoute = r.pointClosestTo(point);
            PointCh p = closestPointOnRoute.point();
            closestPoint = closestPoint.min(p,
                    closestPointOnRoute.position() + tempPosition,
                    p.distanceTo(point));
            tempPosition += r.length();
        }
        return closestPoint;
    }
}
