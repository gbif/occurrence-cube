package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.api.java.UDF4;
import org.gbif.occurrence.cube.functions.Isea3hCellCode;
import org.gbif.occurrence.cube.functions.MilitaryGridReferenceSystemCellCode;

import java.io.Serializable;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * ISEA3H cell in which the randomized point lies.
 */
public class Isea3hCellCodeUdfSpark implements /* Spark */ UDF4<Integer,Double,Double,Double,String>, Serializable {

  private final Isea3hCellCode isea3hCellCode = new Isea3hCellCode();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public String call(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    return isea3hCellCode.fromCoordinate(level, lat, lon, coordinateUncertaintyInMeters);
  }
}
