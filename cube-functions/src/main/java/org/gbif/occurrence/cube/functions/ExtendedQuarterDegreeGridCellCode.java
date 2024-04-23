package org.gbif.occurrence.cube.functions;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Extended Quarter Degree Grid Cell codes.
 *
 * https://doi.org/10.1111/j.1365-2028.2008.00997.x
 */
public class ExtendedQuarterDegreeGridCellCode implements Serializable {

  // TODO: Suitable seed value.

  /**
   * Calculate the Extended Quarter Degree Grid Cell to the specified level, randomizing the coordinate
   * using the given uncertainty circle.
   */
  public String fromCoordinate(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    // sanitize the input, force the user to specify these values
    if (level == null) {
      throw new IllegalArgumentException("level is required");
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
    return extendedQuarterDegreeGridCellCode(level, moved.getY(), moved.getX());
  }

  /*
   * The squares around 0°, 0° are like this; the assigned letters are always in the same positions
   * (nearest the origin is not always A).
   *
   *  AB|AB
   *  CD|CD
   *  --+--
   *  AB|AB
   *  CD|CD
   *
   * Call these squares
   *
   *  I|J
   *  -+-
   *  K|L
   *
   * Then big/small-x, big/small-y values are:
   *  XY xY|xY XY
   *  Xy xy|xy Xy
   *  -----+-----
   *  Xy xy|xy Xy
   *  XY xY|xY XY
   *
   * And storing this in array in this order:
   *  xy , Xy , xY,  XY
   * Gives these arrays for each square:
   */
  private static final char[] I = {'D', 'C', 'B', 'A'};
  private static final char[] J = {'C', 'D', 'A', 'B'};
  private static final char[] K = {'B', 'A', 'D', 'C'};
  private static final char[] L = {'A', 'B', 'C', 'D'};

  private static String extendedQuarterDegreeGridCellCode(int level, double lat, double lon) {
    StringBuilder sb = new StringBuilder();

    if (lon >= 0) {
      sb.append('E');
    } else {
      sb.append('W');
    }
    if (Math.abs(lon) < 100) {
      sb.append('0');
      if (Math.abs(lon) < 10) {
        sb.append('0');
      }
    }
    sb.append((int)Math.floor(Math.abs(lon)));

    if (lat >= 0) {
      sb.append('N');
    } else {
      sb.append('S');
    }
    if (Math.abs(lat) < 10) {
      sb.append('0');
    }
    sb.append((int)Math.floor(Math.abs(lat)));

    double boundary = 1;
    double x = Math.abs(lon - ((int)lon));
    double y = Math.abs(lat - ((int)lat));

    char[] square;

    if (lon > 0) {
      if (lat > 0) {
        square = J;
      } else {
        square = L;
      }
    } else {
      if (lat > 0) {
        square = I;
      } else {
        square = K;
      }
    }

    for (int l = 1; l <= level; l++) {
      boundary /= 2;

      if (x >= boundary) {
        if (y >= boundary) {
          sb.append(square[3]);
          y -= boundary;
        } else {
          sb.append(square[1]);
        }
        x -= boundary;
      } else {
        if (y >= boundary) {
          sb.append(square[2]);
          y -= boundary;
        } else {
          sb.append(square[0]);
        }
      }
    }

    return sb.toString();
  }
}
