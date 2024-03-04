package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.api.java.UDF2;
import org.gbif.occurrence.cube.functions.TemporalUncertainty;

/**
 * Calculate the temporal uncertainty of an eventDate and optional eventTime.
 */
public class TemporalUncertaintyUdfSpark implements /* Spark */ UDF2<String,String,Long> {

  private final TemporalUncertainty temporalUncertainty = new TemporalUncertainty();

  /**
   * Spark SQL UDF method.
   */
  @Override
  public Long call(String eventDate, String eventTime) throws Exception {
    return temporalUncertainty.fromEventDateAndEventTime(eventDate, eventTime);
  }
}
