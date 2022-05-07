package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

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
    public ReadOnlyObjectProperty<Route> route;
    public DoubleProperty highlightedPosition; // la position mise en évidence
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
    }



}
