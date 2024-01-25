package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.api.java.UDF4;
import org.gbif.occurrence.cube.functions.EeaCellCode;
import org.gbif.occurrence.cube.functions.ExtendedQuarterDegreeGridCellCode;

/**
 * Randomize a point according to its coordinateUncertainty (or some other distance), and determine the
 * Extended Quarter Degree Grid Cell in which the randomized point lies.
 */
public class ExtendedQuarterDegreeGridCellCodeUdfSpark implements /* Spark */ UDF4<Integer,Double,Double,Double,String> {

  private final ExtendedQuarterDegreeGridCellCode extendedQuarterDegreeGridCellCode = new ExtendedQuarterDegreeGridCellCode();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public String call(Integer level, Double lat, Double lon, Double coordinateUncertaintyInMeters) throws Exception {
    return extendedQuarterDegreeGridCellCode.fromCoordinate(level, lat, lon, coordinateUncertaintyInMeters);
  }
}
