package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.api.java.UDF4;
import org.gbif.occurrence.cube.functions.EuroStatCellCode;

import java.io.Serializable;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * EuroStat Grid Cell in which the randomized point lies.
 */
public class EuroStatCellCodeUdfSpark implements /* Spark */ UDF4<Integer,Double,Double,Double,String>, Serializable {

  private final EuroStatCellCode euroStatCellCode = new EuroStatCellCode();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public String call(Integer gridSize, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    return euroStatCellCode.fromCoordinate(gridSize, lat, lon, coordinateUncertaintyInMeters);
  }
}
