package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.NaN;

/**
 * Représente un itinéraire multiple, c.-à-d. composé d'une séquence d'itinéraires contigus
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;


    public static void main(String[] args) {
        Edge e = new Edge(0, 1, null, null, 1000, Functions.constant(NaN));
        SingleRoute s1 = new SingleRoute(List.of(e));
        SingleRoute s2 = new SingleRoute(List.of(e));
        SingleRoute s3 = new SingleRoute(List.of(e));
        MultiRoute mr1 = new MultiRoute(List.of(s1, s2, s3));
        MultiRoute mr2 = new MultiRoute(List.of(s1, s2, s3));
        MultiRoute m = new MultiRoute(List.of(mr1, mr2));
        //System.out.println(m.findRouteIndex(5500));
        //System.out.println(mr1.findRouteIndex(5500));
        int a = m.indexOfSegmentAt(435023984);
        System.out.println(a);
    }
    /**
     * Construit un itinéraire multiple composé des segments donnés, ou lève IllegalArgumentException si la liste des segments est vide
     * @param segments
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
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
    /**
     *
     * @param position
     * @return
     */
    @Override
    public int indexOfSegmentAt(double position) {
        int index = findRouteIndex(position);
        Route r = segments.get(index);
        int previousIndexes = 0;
        for (int i = 0; i < index; i++) {
            previousIndexes += segments.get(i).indexOfSegmentAt(position);
        }
        return index + r.indexOfSegmentAt(position) + previousIndexes;
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

    @Override
    public PointCh pointAt(double position) {
        int routeIndex = findRouteIndex(position);
        Route r = segments.get(routeIndex);
        return r.pointAt(position - r.length()); // il faut sans doute ajuster la position pour avoir la position sur s...
    }

    @Override
    public double elevationAt(double position) {
        int routeIndex = findRouteIndex(position);
        Route r = segments.get(routeIndex);
        return r.elevationAt(position - r.length());
    }

    @Override
    public int nodeClosestTo(double position) {
        int routeIndex = findRouteIndex(position);
        Route r = segments.get(routeIndex);
        return r.nodeClosestTo(position - r.length());
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
