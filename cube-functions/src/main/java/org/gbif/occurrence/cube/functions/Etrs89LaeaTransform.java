package org.gbif.occurrence.cube.functions;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.io.Serializable;

/**
 * European Equal Area grid.
 *
 * https://inspire-mif.github.io/technical-guidelines/data/su/dataspecification_su.html#_grids
 */
public abstract class Etrs89LaeaTransform implements Serializable {

  static final String EPSG_3035_WKT = "PROJCS[\"ETRS89 / LAEA Europe\", \n" +
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
    "]";

  private final MathTransform transform;

  // TODO: Suitable seed value.
  // https://able.bio/patrickcording/reproducible-distributed-random-number-generation-in-spark--03tcnko
  // private final Double seed = 7389450.0;

  public Etrs89LaeaTransform() {
    try {
      // Would need the library added.
      // crs = CRS.decode("EPSG:3035");

      CoordinateReferenceSystem crs = CRS.parseWKT(EPSG_3035_WKT);
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
    if (!withinBounds(x, y)) {
      return null;
    }

    // Step 3: assign occurrence within uncertainty circle
    double theta = Math.random() * 2 * Math.PI;
    double r = Math.sqrt(Math.random()) * coordinateUncertaintyInMeters;
    x += r * Math.cos(theta);
    y += r * Math.sin(theta);

    // Step 4: Find grid cell to which the occurrence belongs
    return cellCode(gridSize, x, y);
  }

  abstract boolean withinBounds(double x, double y);

  abstract String cellCode(int gridSize, double x, double y);
}
