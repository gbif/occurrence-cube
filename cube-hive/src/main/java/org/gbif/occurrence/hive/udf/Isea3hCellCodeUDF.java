package org.gbif.occurrence.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import org.gbif.occurrence.cube.functions.Isea3hCellCode;
import org.gbif.occurrence.cube.functions.MilitaryGridReferenceSystemCellCode;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * ISEA3H cell in which the randomized point lies.
 */
@Description(name = "isea3hCode",
  value = "_FUNC_(Integer, Double, Double, Double) - description",
  extended = "Example: isea3hCode(2, decimalLatitude, decimalLongitude, COALESCE(coordinateUncertaintyInMeters, 1000))")
public class Isea3hCellCodeUDF extends UDF {

  private final Isea3hCellCode isea3hCellCode = new Isea3hCellCode();

  public Text evaluate(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) {
    if (gridSize == null || lat == null || lon == null || coordinateUncertaintyInMeters == null) {
      return null;
    }

    try {
      return new Text(isea3hCellCode.fromCoordinate(gridSize, lat, lon, coordinateUncertaintyInMeters));
    } catch (Exception e) {
      return null;
    }
  }
}
