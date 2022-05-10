package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe finale regroupant les propriétés relatives aux points de passage et à l'itinéraire
 * correspondant.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class RouteBean {
    private RouteComputer routeComputer;
    public ObservableList<Waypoint> waypoints;
    public ObjectProperty<Route> route;
    public DoubleProperty highlightedPosition; // la position mise en évidence
    public ObjectProperty<ElevationProfile> elevationProfile;
    private static final int MAX_ENTRIES = 50;
    private static final double MAX_STEP_LENGTH = 5;
    private final Map<Pair<Integer, Integer>, Route> cacheMemory =
            new LinkedHashMap<>(MAX_ENTRIES, .75f, true);

    /**
     * Unique constructeur servant à déterminer le meilleur itinéraire reliant
     * deux points de passage.
     *
     * @param routeComputer le calculateur d'itinéraire utilisé
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        this.waypoints = FXCollections.observableArrayList();
        // pas de waypoints ajoutés pour l'instant
        // encore initialiser d'autres choses
        addListeners();
    }

    private void addListeners() {
        waypoints.addListener((ListChangeListener<? super Waypoint>) e -> {
            if (waypoints.size() >= 2) {
                itineraryComputer();
            } else {
                route.set(null);
            }
            ElevationProfile profile =
                    ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH);
            elevationProfile = new SimpleObjectProperty<>(profile);
        });
    }

    /**
     * Retourne l'itinéraire calculé.
     *
     * @return l'itinéraire calculé
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }


    public Route route() {
        return route.get();
    }

    /**
     * Retourne la propriété de la position mise en évidence.
     *
     * @return la propriété de la position mise en évidence
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Retourne le contenu de la propriété de la position mise en évidence.
     *
     * @return le contenu de la propriété de la position mise en évidence
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    public void setHighlightedPosition(double newHighlightedPosition) {
        this.highlightedPosition.set(newHighlightedPosition);
    }

    // private non ?
    private void itineraryComputer() {
        List<Route> singleRoutes = new ArrayList<>();

        for (int i = 0; i < waypoints.size() - 1; i++) {

            int predecessorWaypointNodeId = waypoints.get(i).closestNodeId();
            int successorWaypointNodeId = waypoints.get(i + 1).closestNodeId();
            Pair<Integer, Integer> pair = new Pair<>(predecessorWaypointNodeId,
                    successorWaypointNodeId);

            if (cacheMemory.containsKey(pair)) {
                singleRoutes.add(cacheMemory.get(pair));
                continue;
            }

            Route singleRoute =
                    routeComputer.bestRouteBetween(predecessorWaypointNodeId,
                            successorWaypointNodeId);

            if (singleRoute == null) {
                route.set(null);
                elevationProfile.set(null);
                break;
            }

            singleRoutes.add(singleRoute);
        }

        route.set(new MultiRoute(singleRoutes));
    }


}
