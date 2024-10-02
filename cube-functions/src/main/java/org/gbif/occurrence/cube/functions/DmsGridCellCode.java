package org.gbif.occurrence.cube.functions;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Grid cell codes in an invented degrees-minutes-seconds format, with rounding to a given number of seconds.
 */
public class DmsGridCellCode implements Serializable {

  // TODO: Suitable seed value.

  /**
   * Grid cell codes in an invented degrees-minutes-seconds format, with rounding to a given number of seconds.
   *
   * Randomize the coordinate using the given uncertainty circle.
   */
  public String fromCoordinate(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    // sanitize the input, force the user to specify these values
    if (level == null) {
      throw new IllegalArgumentException("level is required");
    }
    if (coordinateUncertaintyInMeters == null || lat == null || lon == null) {
      return null;
    }
    if (level < 1 || 3600 % level != 0) {
      throw new IllegalArgumentException("level must be a positive divisor of 3600");
    }
    if (lat > 90 || lat < -90 || lon > 180 || lon < -180) {
      throw new IllegalArgumentException("Latitude and longitude must be within ±90° and ±180°.");
    }

    // Reproject the coordinate
    Point2D moved = RandomizeCoordinate.moveCoordinate(new Point2D.Double(lon, lat), coordinateUncertaintyInMeters);

    // Find grid cell to which the occurrence belongs
    return dmsGridCellCode(level, moved.getY(), moved.getX());
  }

  protected static String dmsGridCellCode(int divisor, double lat, double lon) {
    StringBuilder sbLat = new StringBuilder();
    StringBuilder sbLon = new StringBuilder();

    int dLat;
    int dLon;
    dLat = (int) Math.floor(Math.abs(lat));
    dLon = (int) Math.floor(Math.abs(lon));

    // Integer parts of longitude and latitude.
    if (lon >= 0) {
      sbLon.append('E');
    } else {
      sbLon.append('W');
      // Decrement the negative longitude to account for there being two zeros, E000 and W000.
      if ((lon % 1) == 0) {
        dLon -= 1;
      }
    }
    if (Math.abs(dLon) < 100) {
      sbLon.append('0');
      if (Math.abs(dLon) < 10) {
        sbLon.append('0');
      }
    }
    sbLon.append(dLon); // °

    if (lat >= 0) {
      sbLat.append('N');
    } else {
      sbLat.append('S');
      // Decrement the negative latitude to account for there being two zeros, N00 and S00.
      if ((lat % 1) == 0) {
        dLat -= 1;
      }
    }
    if (Math.abs(dLat) < 10) {
      sbLat.append('0');
    }
    sbLat.append(dLat); // °

    if (divisor == 3600) {
      // Degrees only
      sbLon.append(sbLat);
      return sbLon.toString();
    } else if (divisor % 60 == 0) {
      // Minutes only.
      int factor = 3600 / divisor;
      int multiple = 60 / factor;

      int mLat = (int) Math.floor((Math.abs(lat)*factor)%factor)*multiple;
      int mLon = (int) Math.floor((Math.abs(lon)*factor)%factor)*multiple;

      if (Math.abs(mLat) < 10) { sbLat.append('0'); }
      sbLat.append(mLat); // ′
      if (Math.abs(mLon) < 10) { sbLon.append('0'); }
      sbLon.append(mLon); // ′

      sbLon.append(sbLat);
      return sbLon.toString();
    } else {
      // Round to given seconds
      int factor = 3600 / divisor;
      int sLat = (int) Math.floor((Math.abs(lat)*factor)%factor)*divisor;
      int sLon = (int) Math.floor((Math.abs(lon)*factor)%factor)*divisor;

      // Find minutes
      int mLat = sLat / 60;
      int mLon = sLon / 60;
      if (Math.abs(mLat) < 10) { sbLat.append('0'); }
      sbLat.append(mLat); // ′
      if (Math.abs(mLon) < 10) { sbLon.append('0'); }
      sbLon.append(mLon); // ′

      // Remaining seconds
      sLat = sLat % 60;
      sLon = sLon % 60;
      if (Math.abs(sLat) < 10) { sbLat.append('0'); }
      sbLat.append(sLat); // ″
      if (Math.abs(sLon) < 10) { sbLon.append('0'); }
      sbLon.append(sLon); // ″

      sbLon.append(sbLat);
      return sbLon.toString();
    }
  }
}
