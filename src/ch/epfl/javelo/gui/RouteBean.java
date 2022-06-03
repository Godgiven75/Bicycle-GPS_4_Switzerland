package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * Classe finale du Bean JavaFX regroupant les propriétés relatives aux points de passage et à
 * l'itinéraire correspondant.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class RouteBean {
    private final RouteComputer routeComputer;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> routeP;
    private final DoubleProperty highlightedPositionP; // la position mise en évidence
    private final ObjectProperty<ElevationProfile> elevationProfileP;
    private static final int MAX_ENTRIES = 50;
    private static final double MAX_STEP_LENGTH = 5d;
    // Le cache mémoire a une capacité de 50 routes
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
        this.highlightedPositionP = new SimpleDoubleProperty();
        this.routeP = new SimpleObjectProperty<>();
        this.elevationProfileP = new SimpleObjectProperty<>();
        addListeners();
    }

    /**
     * Retourne la propriété contenant l'itinéraire calculé.
     *
     * @return la proproété contenant l'itinéraire calculé
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeP;
    }

    /**
     * Retourne l'itinéraire calculé.
     *
     * @return l'itinéraire calculé
     */
    public Route route() {
        return routeP.get();
    }

    /**
     * Retourne la propriété contenant la position mise en évidence.
     *
     * @return la propriété contenant  la position mise en évidence
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPositionP;
    }

    /**
     * Retourne la propriété contenant le profil d'élévation.
     *
     * @return la propriété contenant le profil d'élévation
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfileP;
    }

    /**
     * Retourne le profil d'élévation de l'itinéraire.
     * @return le profil d'élévation de l'itinéraire
     */
    public ElevationProfile elevationProfile() {
        return elevationProfileP.get();
    }

    /**
     * Retourne la position mise en évidence.
     *
     * @return la position mise en évidence
     */
    public double highlightedPosition() {
        return highlightedPositionP.get();
    }

    /**
     * Assigne la propriété contenant la position mise en évidence à la valeur
     * passée en argument.
     *
     * @param newHighlightedPosition nouvelle valeur de la propriété contenant
     *
     * la position mise en évidence
     */
    public void setHighlightedPositionProperty(double newHighlightedPosition) {
        this.highlightedPositionP.set(newHighlightedPosition);
    }

    /**
     * Retourne la liste des points de passage de l'itinéraire.
     *
     * @return la liste des points de passage de l'itinéraire
     */
    public ObservableList<Waypoint> waypoints() {
        return waypoints;
    }

    /**
     * Retourne l'indice du segment non vide à la position passée en argument.
     *
     * @author M. Schinz
     *
     * @param position position le long de l'itinéraire
     *
     * @return l'indice du segment non vide à la position passée en argument
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i++) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    // Ajoute les listeners sur la liste observable des points de passage.
    private void addListeners() {
        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            Route itinerary = computeItinerary();
            if (itinerary == null) {
                routeP.set(null);
                elevationProfileP.set(null);
                return;
            }
            routeP.set(itinerary);
            ElevationProfile profile =
                    ElevationProfileComputer.elevationProfile(itinerary, MAX_STEP_LENGTH);
            elevationProfileP.set(profile);
        });
    }

    // Crée la route correspondant à l'itinéraire.
    private Route computeItinerary() {
        List<Route> singleRoutes = new ArrayList<>();

        for (int i = 0; i < waypoints.size() - 1; i++) {
            int predecessorWaypointNodeId = waypoints.get(i).closestNodeId();
            int successorWaypointNodeId = waypoints.get(i + 1).closestNodeId();
            // Si les noeuds sont identiques, on ne fait pas de tentative de
            // calcul d'itinéraire
            if (predecessorWaypointNodeId == successorWaypointNodeId)
                return null;
            Pair<Integer, Integer> pair = new Pair<>(predecessorWaypointNodeId,
                    successorWaypointNodeId);


            // Si le cache mémoire  contient déjà une route entre les deux points,
            // on l'ajoute directement aux segments de l'itinéraire multiple
            if (cacheMemory.containsKey(pair)) {
                singleRoutes.add(cacheMemory.get(pair));
                continue;
            }
            Route singleRoute =
                    routeComputer.bestRouteBetween(predecessorWaypointNodeId,
                            successorWaypointNodeId);
            // Si un des itinéraires simples est null, on retourne null
            if (singleRoute == null)
                return null;

            singleRoutes.add(singleRoute);

            // Si le cache mémoire est plein, alors on retire la route calculée
            // le moins récemment
            if (cacheMemory.size() >= MAX_ENTRIES)
                cacheMemory.remove(cacheMemory.keySet().iterator().next());
            // Ajout de la dernière route calculée au cache mémoire
            cacheMemory.put(pair, singleRoute);
        }
        //S'il y a moins de deux points de passage, on retourne également null
        if (singleRoutes.isEmpty())
            return null;
        return new MultiRoute(singleRoutes);
    }
}