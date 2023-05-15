package org.gbif.graph.clustering.udf;

import org.apache.spark.sql.api.java.UDF4;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * EEA Reference Grid Cell in which the randomized point lies.
 */
public class EeaCellCodeUdf implements UDF4<Integer,Double,Double,Double,String> {

  // SEED?
  // https://able.bio/patrickcording/reproducible-distributed-random-number-generation-in-spark--03tcnko
  private static final Double SEED = 7389450.0;

  MathTransform transform;

  public EeaCellCodeUdf() {
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

      transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs,true);
    } catch (Exception e) {}
  }

  @Override
  public String call(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
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

    // Step 1: Reproject the coordinate
    double[] srcPt = new double[]{lon, lat, 0}; // EAST_NORTH order for WGS84.
    double[] dstPt = new double[3];
    transform.transform(srcPt, 0, dstPt, 0, 1);

    double x = dstPt[1]; // Axis order NORTH_EAST for EPSG:3035
    double y = dstPt[0];

    // Step 2: assign occurrence within uncertainty circle
    double theta = Math.random() * 2 * Math.PI;
    double r = Math.sqrt(Math.random()) * coordinateUncertaintyInMeters;
    x += r * Math.cos(theta);
    y += r * Math.sin(theta);

    // Step 3: Find grid cell to which the occurrence belongs
    return eeaCellCode(gridSize, x, y);
  }

  private static String eeaCellCode(int gridSize, double x, double y) {
    int order = 0;
    int t = gridSize;
    while (t % 10 == 0 && t != 0) {
      t /= 10;
      order++;
    }

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
    sb.append((int) Math.floor(x / Math.pow(10, order)));

    if (y >= 0) {
      sb.append("N");
    } else {
      y = Math.abs(y);
      sb.append("S");
    }
    sb.append((int) Math.floor(y / Math.pow(10, order)));

    return sb.toString();
  }
}
