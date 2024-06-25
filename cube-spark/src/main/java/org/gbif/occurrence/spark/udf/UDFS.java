package org.gbif.occurrence.spark.udf;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

public class UDFS {

  public static void registerUdfs(SparkSession sparkSession) {
    sparkSession.udf().register("eeaCellCode", new EeaCellCodeUdfSpark(), DataTypes.StringType);
    sparkSession.udf().register("mgrsCellCode", new MilitaryGridReferenceSystemCellCodeUdfSpark(), DataTypes.StringType);
    sparkSession.udf().register("isea3hCellCode", new Isea3hCellCodeUdfSpark(), DataTypes.StringType);
    sparkSession.udf().register("qdgcCode", new ExtendedQuarterDegreeGridCellCodeUdfSpark(), DataTypes.StringType);
    sparkSession.udf().register("stringArrayContains", new StringArrayContainsGenericUdf(), DataTypes.BooleanType);
  }
}
