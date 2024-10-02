package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.api.java.UDF4;
import org.gbif.occurrence.cube.functions.DmsGridCellCode;
import org.gbif.occurrence.cube.functions.ExtendedQuarterDegreeGridCellCode;

import java.io.Serializable;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * rounded Degree-Minute-Second Grid Cell in which the randomized point lies.
 */
public class DmsGridCellCodeUdfSpark implements /* Spark */ UDF4<Integer,Double,Double,Double,String>, Serializable {

  private final DmsGridCellCode dmsGridCellCode = new DmsGridCellCode();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public String call(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    return dmsGridCellCode.fromCoordinate(level, lat, lon, coordinateUncertaintyInMeters);
  }
}
