package org.gbif.occurrence.cube.functions;

import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.grid.GridType;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Extended Quarter Degree Grid Cell codes.
 *
 * https://doi.org/10.1111/j.1365-2028.2008.00997.x
 */
public class MilitaryGridReferenceSystemCellCode implements Serializable {

  // TODO: Suitable seed value.

  /**
   * Calculate the Extended Quarter Degree Grid Cell to the specified level, randomizing the coordinate
   * using the given uncertainty circle.
   */
  public String fromCoordinate(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    // sanitize the input, force the user to specify these values
    if (gridSize == null) {
      throw new IllegalArgumentException("gridSize is required");
    }
    if (coordinateUncertaintyInMeters == null || lat == null || lon == null) {
      return null;
    }
    if (lat > 90 || lat < -90 || lon > 180 || lon < -180) {
      throw new IllegalArgumentException("Latitude and longitude must be within ±90° and ±180°.");
    }

    // Reproject the coordinate
    Point2D moved = RandomizeCoordinate.moveCoordinate(new Point2D.Double(lon, lat), coordinateUncertaintyInMeters);

    // Find grid cell to which the occurrence belongs
    return militaryGridReferenceSystemCellCode(gridSize, moved.getY(), moved.getX());
  }

  private static String militaryGridReferenceSystemCellCode(int gridSize, double lat, double lon) {
    if (lat > 84 || lat < -80) {
      return null;
    }

    Point p = Point.point(lon, lat);

    if (gridSize == 0) {
      MGRS mgrs = MGRS.from(p);
      return (mgrs.getZone() < 10 ? "0" : "") + mgrs.getGridZone().getName();
    } else {
      GridType gridType = GridType.getPrecision(gridSize);
      MGRS mgrs = MGRS.from(p);
      return (mgrs.getZone() < 10 ? "0" : "") + mgrs.coordinate(gridType);
    }
  }
}
