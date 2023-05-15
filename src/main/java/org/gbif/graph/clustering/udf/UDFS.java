package org.gbif.graph.clustering.udf;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

public class UDFS {

  public static void registerUdfs(SparkSession sparkSession) {
    sparkSession.udf().register("eeaCellCode", new EeaCellCodeUdf(), DataTypes.StringType);
    sparkSession.udf().register("stringArrayContains", new StringArrayContainsGenericUdf(), DataTypes.BooleanType);
  }

}
