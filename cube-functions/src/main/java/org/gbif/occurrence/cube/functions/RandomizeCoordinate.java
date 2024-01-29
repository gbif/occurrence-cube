package org.gbif.occurrence.cube.functions;

import org.geotools.referencing.GeodeticCalculator;

import java.awt.geom.Point2D;

/**
 * Move a coordinate from its origin a random distance at a random angle.
 */
public class RandomizeCoordinate {

  public static Point2D moveCoordinate(Point2D point, Double coordinateUncertaintyInMeters) {
    if (coordinateUncertaintyInMeters == null) {
      return null;
    }
    if (coordinateUncertaintyInMeters <= 0) {
      return point;
    }

    GeodeticCalculator calc = new GeodeticCalculator();
    calc.setStartingGeographicPoint(point);
    double azimuth = Math.random() * 2 * Math.PI;
    double distance = Math.sqrt(Math.random()) * coordinateUncertaintyInMeters;
    calc.setDirection(azimuth, distance);
    return calc.getDestinationGeographicPoint();
  }
}
