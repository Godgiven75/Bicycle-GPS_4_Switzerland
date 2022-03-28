package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un itinéraire multiple, càd composé d'une séquence d'itinéraires contigus nommés segments
 */
public class MultiRoute implements Route {
    List<Route> segments;

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double length = 0;
        for (Route r : segments) {
            length += r.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
       List<Edge> edges = new ArrayList<>();
       for (Route r : segments) {
           for (Edge e : r.edges()) {
               edges.add(e);
           }
       }
       return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> l = new ArrayList<>();
        for (Edge e : this.edges()) {
            l.add(e.fromPoint());
        }
        l.add(this.edges().get(edges().size() - 1).toPoint());
        return l;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
