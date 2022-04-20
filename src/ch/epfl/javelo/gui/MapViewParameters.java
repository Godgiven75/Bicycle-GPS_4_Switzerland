package ch.epfl.javelo.gui;

import javafx.geometry.Point2D;

public record MapViewParameters(int zoomLevel, double xTopLeft,
                                double yTopLeft) {

    public Point2D topLeft() {
        double x;
        double y;
        return new Point2D();
    }

}
