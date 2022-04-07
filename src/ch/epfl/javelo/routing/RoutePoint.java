package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * Représente le point d'un itinéraire le plus proche d'un point de référence
 * donné, qui se trouve dans le voisinage de l'itinéraire.
 *
 * @param point le point sur l'itinéraire
 * @param position la position du point le long de l'itinéraire, en mètres
 * @param distanceToReference la distance, en mètres, entre le point et la référence
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {
    /**
     * Représente un point inexistant.
     */
    public static final RoutePoint NONE
            = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Retourne un point identique au récepteur (this) mais dont la position est
     * décalée de la différence de position donnée.
     *
     * @param positionDifference la différence donnée
     *
     * @return un point identique au récepteur, mais décalé de la différence de
     * position donnée
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        double shiftedPosition = this.position + positionDifference;
        return new RoutePoint(point, shiftedPosition, distanceToReference);
    }

    /**
     * Retourne this si sa distance à la référence est inférieure ou égale à
     * celle de that, et that sinon.
     *
     * @param that autre point
     *
     * @return this si sa distance à la référence est inférieure ou égale à
     * celle de that, et that sinon
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * Retourne this si sa distance à la référence est inférieure ou égale à
     * thatDistanceToReference, et une nouvelle instance de RoutePoint avec pour
     * arguments, thatPoint, thatPosition et thatDistanceToReference sinon.
     *
     * @param thatPoint autre point sur l'itinéraire
     * @param thatPosition autre position du point le long de l'itinéraire, en mètres
     * @param thatDistanceToReference distance, en mètres, entre l'autre point et la référence
     *
     * @return this si sa distance à la référence est inférieure ou égale à
     * thatDistanceToReference, et une nouvelle instance de RoutePoint avec pour
     * arguments, thatPoint, thatPosition et thatDistanceToReference sinon
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ?
                this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
