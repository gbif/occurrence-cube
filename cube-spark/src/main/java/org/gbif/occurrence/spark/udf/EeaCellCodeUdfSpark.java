package org.gbif.occurrence.spark.udf;

import java.io.Serializable;
import org.apache.spark.sql.api.java.UDF4;
import org.gbif.occurrence.cube.functions.EeaCellCode;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * EEA Reference Grid Cell in which the randomized point lies.
 */
public class EeaCellCodeUdfSpark implements /* Spark */ UDF4<Integer,Double,Double,Double,String>, Serializable {

  private final EeaCellCode eeaCellCode = new EeaCellCode();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public String call(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    return eeaCellCode.fromCoordinate(gridSize, lat, lon, coordinateUncertaintyInMeters);
  }
}
