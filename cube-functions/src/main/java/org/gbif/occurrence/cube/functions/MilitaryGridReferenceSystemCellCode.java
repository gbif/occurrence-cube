package org.gbif.occurrence.cube.functions;

import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.grid.GridType;

/**
 * Extended Quarter Degree Grid Cell codes.
 *
 * https://doi.org/10.1111/j.1365-2028.2008.00997.x
 */
public class MilitaryGridReferenceSystemCellCode {

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
    if (coordinateUncertaintyInMeters == null) {
      return null;
    }
    if (lat == null || lon == null) {
      return null;
    }

    // Assign occurrence within uncertainty circle
    double theta = Math.random() * 2 * Math.PI;
    double r = Math.sqrt(Math.random()) * coordinateUncertaintyInMeters;
    lon += r * Math.cos(theta);
    lat += r * Math.sin(theta);

    // Find grid cell to which the occurrence belongs
    return militaryGridReferenceSystemCellCode(gridSize, lat, lon);
  }

  private static String militaryGridReferenceSystemCellCode(int gridSize, double lat, double lon) {
    if (lat > 84 || lat < -80) {
      return null;
    }

    GridType gridType = GridType.getPrecision(gridSize);
    Point p = Point.point(lon, lat);
    MGRS mgrs = MGRS.from(p);
    return mgrs.coordinate(gridType);
  }
}
