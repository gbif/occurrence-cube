package org.gbif.occurrence.cube.functions;

import org.geotools.referencing.GeodeticCalculator;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Move a coordinate from its origin a random distance at a random angle.
 */
public class RandomizeCoordinate implements Serializable {

  public static Point2D moveCoordinate(Point2D point, Double coordinateUncertaintyInMeters) {
    if (coordinateUncertaintyInMeters == null) {
      return null;
    }
    if (coordinateUncertaintyInMeters <= 0) {
      return point;
    }

    GeodeticCalculator calc = new GeodeticCalculator();
    calc.setStartingGeographicPoint(point);
    double azimuth = Math.random() * 360;
    double distance = Math.sqrt(Math.random()) * coordinateUncertaintyInMeters;
    calc.setDirection(azimuth, distance);
    return calc.getDestinationGeographicPoint();
  }
}
