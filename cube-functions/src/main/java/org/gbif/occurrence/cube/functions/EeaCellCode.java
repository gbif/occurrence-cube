package org.gbif.occurrence.cube.functions;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.Serializable;

/**
 * European Economic Area Reference Grid.
 *
 * https://www.eea.europa.eu/data-and-maps/data/eea-reference-grids-2/about-the-eea-reference-grid/eea_reference_grid_v1.pdf/download
 */
public class EeaCellCode implements Serializable {

  private final MathTransform transform;

  // TODO: Suitable seed value.
  // https://able.bio/patrickcording/reproducible-distributed-random-number-generation-in-spark--03tcnko
  // private final Double seed = 7389450.0;

  public EeaCellCode() {
    try {
      // Would need the library added.
      // crs = CRS.decode("EPSG:3035");

      CoordinateReferenceSystem crs = CRS.parseWKT("PROJCS[\"ETRS89 / LAEA Europe\", \n" +
        "  GEOGCS[\"ETRS89\", \n" +
        "    DATUM[\"European Terrestrial Reference System 1989\", \n" +
        "      SPHEROID[\"GRS 1980\", 6378137.0, 298.257222101, AUTHORITY[\"EPSG\",\"7019\"]], \n" +
        "      TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], \n" +
        "      AUTHORITY[\"EPSG\",\"6258\"]], \n" +
        "    PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], \n" +
        "    UNIT[\"degree\", 0.017453292519943295], \n" +
        "    AXIS[\"Geodetic latitude\", NORTH], \n" +
        "    AXIS[\"Geodetic longitude\", EAST], \n" +
        "    AUTHORITY[\"EPSG\",\"4258\"]], \n" +
        "  PROJECTION[\"Lambert_Azimuthal_Equal_Area\", AUTHORITY[\"EPSG\",\"9820\"]], \n" +
        "  PARAMETER[\"latitude_of_center\", 52.0], \n" +
        "  PARAMETER[\"longitude_of_center\", 10.0], \n" +
        "  PARAMETER[\"false_easting\", 4321000.0], \n" +
        "  PARAMETER[\"false_northing\", 3210000.0], \n" +
        "  UNIT[\"m\", 1.0], \n" +
        "  AXIS[\"Northing\", NORTH], \n" +
        "  AXIS[\"Easting\", EAST] \n" +
        "]");
      this.transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs,true);
    } catch (Exception e) {
      throw new RuntimeException("Unable to load EPSG:3035 coordinate transform");
    }
  }

  /**
   * Calculate the EEA reference grid cell code for a given grid size, coordinate and uncertainty.
   *
   * Randomize the coordinate within its uncertainty circle.
   */
  public String fromCoordinate(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    // sanitize the input, force the user to specify these values
    if (gridSize == null || gridSize < 1 || gridSize >= 1_000_000) {
      throw new IllegalArgumentException("gridSize must be between 1 and 999999m");
    }
    if (coordinateUncertaintyInMeters == null || lat == null || lon == null) {
      return null;
    }
    if (lat > 90 || lat < -90 || lon > 180 || lon < -180) {
      throw new IllegalArgumentException("Latitude and longitude must be within ±90° and ±180°.");
    }

    // Step 1: Reproject the coordinate
    double[] srcPt = new double[]{lon, lat, 0}; // EAST_NORTH order for WGS84.
    double[] dstPt = new double[3];
    transform.transform(srcPt, 0, dstPt, 0, 1);

    double x = dstPt[1]; // Axis order NORTH_EAST for EPSG:3035
    double y = dstPt[0];

    // Step 2: Limit to the extent of the EPSG:3035 ETRS89-extended projection
    // Limits are the projected bounds from https://epsg.io/3035
    if (x < 1_896_628.62 || x > 7_104_179.2 || y < 1_095_703.18 || y > 6_882_401.15) {
      return null;
    }

    // Step 3: assign occurrence within uncertainty circle
    double theta = Math.random() * 2 * Math.PI;
    double r = Math.sqrt(Math.random()) * coordinateUncertaintyInMeters;
    x += r * Math.cos(theta);
    y += r * Math.sin(theta);

    // Step 4: Find grid cell to which the occurrence belongs
    return eeaCellCode(gridSize, x, y);
  }

  private static String eeaCellCode(int gridSize, double x, double y) {
    // Find order (number of zeros) of the gridSize
    int order = 0;
    int t = gridSize;
    while (t % 10 == 0 && t != 0) {
      t /= 10;
      order++;
    }

    // Format the grid size using m or km.
    int o = order;
    StringBuilder sb = new StringBuilder(String.valueOf(t));
    if (o % 3 != 0) {
      sb.append('0');
      o--;
    }
    if (o % 3 != 0) {
      sb.append('0');
      o--;
    }
    if (o == 3) {
      sb.append('k');
    }
    sb.append('m');

    if (x >= 0) {
      sb.append("E");
    } else {
      x = Math.abs(x);
      sb.append("W");
    }
    // Find the integer part, then round (floor) it with the required divisor
    double xMagnitude = x / Math.pow(10, order);
    double xFloored = t*Math.floor(xMagnitude/t);
    sb.append((int) xFloored);


    if (y >= 0) {
      sb.append("N");
    } else {
      y = Math.abs(y);
      sb.append("S");
    }
    // Find the integer part, then round (floor) it with the required divisor
    double yMagnitude = y / Math.pow(10, order);
    double yFloored = t*Math.floor(yMagnitude/t);
    sb.append((int) yFloored);

    return sb.toString();
  }
}
