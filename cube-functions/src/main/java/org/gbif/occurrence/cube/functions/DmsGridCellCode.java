package org.gbif.occurrence.cube.functions;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Grid Cell codes in a degree-minute-seconds format using the recommendation from the INSPIRE Data Specification on Statistical Units – Technical Guidelines
 *
 * https://knowledge-base.inspire.ec.europa.eu/publications/inspire-data-specification-statistical-units-technical-guidelines_en
 */
public class DmsGridCellCode implements Serializable {

  /**
   * Grid Cell codes in a degree-minute-seconds format using the recommendation from the INSPIRE Data Specification on Statistical Units – Technical Guidelines
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
    // Use the EuroStat Grid Cell code recommendation: https://inspire-mif.github.io/technical-guidelines/data/su/dataspecification_su.html#_grid_package
    // The cell code is built according to the following pattern:
    // CRS<EPSGcode>*RES*(<size>mN<northing>E<easting>)|(<d>-<m>-<s>dmsLON<d>-<m>-<s>LAT<d>-<m>-<s>)
    // This code is composed of:
    // A coordinate reference system part, represented by the word CRS, followed by the EPSG code.
    // A resolution and position part, which can be:
    // if the coordinate reference system is projected, the word RES followed by the grid resolution in meters and the letter m. Then, the letter N followed by the northing value in meters, and the letter E followed by the easting value in meters too,
    // if the coordinate reference system is not projected, the word RES followed by the grid resolution in degree-minute-second, followed by the word dms. Then the word LON followed by the longitude value in degree-minute-second, and word LAT followed by the latitude value in degree-minute-second.

    StringBuilder sb = new StringBuilder("CRS4326RES");

    if (divisor == 3600) {
      sb.append("1-0-0");
    } else if (divisor % 60 == 0) {
      // Minutes only.
      int factor = 3600 / divisor;
      int multiple = 60 / factor;

      sb.append("0-");
      sb.append(multiple);
      sb.append("-0");
    } else {
      int factor = 3600 / divisor;
      int multiple = 60 / factor;

      sb.append("0-");
      sb.append(multiple);
      sb.append("-");
      sb.append(divisor - multiple * 60);
    }

    String dLatSgn = lat < 0 ? "-" : "";
    String dLonSgn = lon < 0 ? "-" : "";

    // Knocking negative numbers back one step on the grid, then rounding as if they are positive gives
    // the lower left corner of the cell.
    if (lat < 0) {
      lat = Math.nextDown(Math.abs(lat - divisor/3600.0));
    }
    if (lon < 0) {
      lon = Math.nextDown(Math.abs(lon - divisor/3600.0));
    }

    int dLat;
    int dLon;
    dLat = (int) Math.floor(lat);
    dLon = (int) Math.floor(lon);

    // Remove integer part
    lat -= dLat;
    lon -= dLon;
    BigDecimal x = new BigDecimal(52.3);

    // Round to given seconds
    int factor = 3600 / divisor;
    int sLat = (int) Math.floor((lat*factor)%factor)*divisor;
    int sLon = (int) Math.floor((lon*factor)%factor)*divisor;

    // Find minutes
    int mLat = sLat / 60;
    int mLon = sLon / 60;

    // Remaining seconds
    sLat = sLat % 60;
    sLon = sLon % 60;

    sb.append("LON").append(dLonSgn).append(dLon).append('-').append(mLon).append('-').append(sLon);
    sb.append("LAT").append(dLatSgn).append(dLat).append('-').append(mLat).append('-').append(sLat);
    return sb.toString();
  }
}
