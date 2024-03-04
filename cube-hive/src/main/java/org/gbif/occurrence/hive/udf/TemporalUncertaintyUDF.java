package org.gbif.occurrence.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.LongWritable;
import org.gbif.occurrence.cube.functions.TemporalUncertainty;

/**
 * Calculate the temporal uncertainty of an eventDate and optional eventTime.
 */
@Description(name = "temporalUncertainty",
  value = "_FUNC_(String, String) - Calculate the temporal uncertainty of an eventDate and optional eventTime",
  extended = "Example: eventDate(eventDate, eventTime)")
public class TemporalUncertaintyUDF extends UDF {

  private final TemporalUncertainty temporalUncertainty = new TemporalUncertainty();

  public LongWritable evaluate(String eventDate, String eventTime) {
    if (eventDate == null) {
      return null;
    }

    final LongWritable resultString = new LongWritable();

    try {
      resultString.set(temporalUncertainty.fromEventDateAndEventTime(eventDate, eventTime));
      return resultString;
    } catch (Exception e) {
      return null;
    }
  }
}
