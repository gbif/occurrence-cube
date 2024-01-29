package org.gbif.occurrence.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import org.gbif.occurrence.cube.functions.MilitaryGridReferenceSystemCellCode;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * Military Grid Reference System cell in which the randomized point lies.
 */
@Description(name = "mgrsCode",
  value = "_FUNC_(Integer, Double, Double, Double) - description",
  extended = "Example: mgrsCode(2, decimalLatitude, decimalLongitude, COALESCE(coordinateUncertaintyInMeters, 1000))")
public class MilitaryGridReferenceSystemCellCodeUDF extends UDF {

  private final MilitaryGridReferenceSystemCellCode militaryGridReferenceSystemCellCode = new MilitaryGridReferenceSystemCellCode();

  public Text evaluate(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    if (gridSize == null || lat == null || lon == null || coordinateUncertaintyInMeters == null) {
      return null;
    }

    try {
      return new Text(militaryGridReferenceSystemCellCode.fromCoordinate(gridSize, lat, lon, coordinateUncertaintyInMeters));
    } catch (Exception e) {
      return null;
    }
  }
}
