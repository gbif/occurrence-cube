package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.api.java.UDF4;
import org.gbif.occurrence.cube.functions.MilitaryGridReferenceSystemCellCode;

import java.io.Serializable;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * Military Grid Reference System cell in which the randomized point lies.
 */
public class MilitaryGridReferenceSystemCellCodeUdfSpark implements /* Spark */ UDF4<Integer,Double,Double,Double,String>, Serializable {

  private final MilitaryGridReferenceSystemCellCode militaryGridReferenceSystemCellCode = new MilitaryGridReferenceSystemCellCode();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public String call(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    return militaryGridReferenceSystemCellCode.fromCoordinate(level, lat, lon, coordinateUncertaintyInMeters);
  }
}
