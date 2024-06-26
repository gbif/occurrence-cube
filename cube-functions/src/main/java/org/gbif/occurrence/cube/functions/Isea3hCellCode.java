package org.gbif.occurrence.cube.functions;

import org.giscience.utils.geogrid.cells.GridCell;
import org.giscience.utils.geogrid.grids.ISEA3H;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * ISEA3H Discrete Global Grid System.
 *
 * Cell identifiers from http://doi.org/10.1080/15230406.2018.1455157
 *
 * The R package has a good overview: https://cran.r-project.org/web/packages/dggridR/vignettes/dggridR.html
 */
public class Isea3hCellCode implements Serializable {

  // TODO: Suitable seed value.

  /**
   * Calculate the ISEA3H cell to the specified resolution, randomizing the coordinate
   * using the given uncertainty circle.
   */
  public String fromCoordinate(Integer resolution, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    // sanitize the input, force the user to specify these values
    if (resolution == null || resolution < 1 || resolution > 22) {
      throw new IllegalArgumentException("resolution is required and must be between 1 and 22");
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
    return isea3hCellCode(resolution, moved.getY(), moved.getX());
  }

  private static String isea3hCellCode(int resolution, double lat, double lon) {
    try {
      ISEA3H grid = new ISEA3H(resolution);

      GridCell gc = grid.cellForLocation(lat, lon);

      return gc.getID().toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
