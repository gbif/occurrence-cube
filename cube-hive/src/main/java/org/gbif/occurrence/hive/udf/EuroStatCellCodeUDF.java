package org.gbif.occurrence.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import org.gbif.occurrence.cube.functions.EuroStatCellCode;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * EuroStat Grid Cell in which the randomized point lies.
 */
@Description(name = "euroStatCellCode",
  value = "_FUNC_(Integer, Double, Double, Double) - description",
  extended = "Example: euroStatCellCode(1000, decimalLatitude, decimalLongitude, COALESCE(coordinateUncertaintyInMeters, 1000))")
public class EuroStatCellCodeUDF extends UDF {

  private final EuroStatCellCode euroStatCellCode = new EuroStatCellCode();

  public Text evaluate(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    if (gridSize == null || lat == null || lon == null || coordinateUncertaintyInMeters == null) {
      return null;
    }

    final Text resultString = new Text();

    try {
      resultString.set(euroStatCellCode.fromCoordinate(gridSize, lat, lon, coordinateUncertaintyInMeters));
      return resultString;
    } catch (Exception e) {
      return null;
    }
  }
}
