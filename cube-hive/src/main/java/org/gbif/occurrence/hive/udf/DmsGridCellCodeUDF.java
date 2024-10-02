package org.gbif.occurrence.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import org.gbif.occurrence.cube.functions.DmsGridCellCode;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * rounded Degree-Minute-Second Grid Cell in which the randomized point lies.
 */
@Description(name = "qdgcCode",
  value = "_FUNC_(Integer, Double, Double, Double) - description",
  extended = "Example: dmsgcCode(30, decimalLatitude, decimalLongitude, COALESCE(coordinateUncertaintyInMeters, 1000))")
public class DmsGridCellCodeUDF extends UDF {

  private final DmsGridCellCode dmsGridCellCode = new DmsGridCellCode();

  public Text evaluate(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    if (level == null || lat == null || lon == null || coordinateUncertaintyInMeters == null) {
      return null;
    }

    try {
      return new Text(dmsGridCellCode.fromCoordinate(level, lat, lon, coordinateUncertaintyInMeters));
    } catch (Exception e) {
      return null;
    }
  }
}

