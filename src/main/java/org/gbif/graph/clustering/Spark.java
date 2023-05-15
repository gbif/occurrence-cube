package org.gbif.graph.clustering;

import org.apache.spark.sql.SparkSession;
import org.gbif.graph.clustering.udf.UDFS;

import java.io.File;

/**
 * Spark SQL reproduction of the data cube in https://doi.org/10.5281/zenodo.7389450 using the method
 * described in https://doi.org/10.1101/2020.03.23.983601.
 */
public class Spark {

  public static void main(String[] args) throws Exception {
    String warehouseLocation = new File("spark-warehouse").getAbsolutePath();
    SparkSession spark = SparkSession
      .builder()
      .appName("B³ data cube")
      .config("spark.sql.warehouse.dir", warehouseLocation)
      .enableHiveSupport()
      .getOrCreate();

    UDFS.registerUdfs(spark);

    String table = "datacube_si_csv_20230522";

    spark.sql(
        "SELECT " +
          "  year, " +
          "  eeaCellCode(1000, decimalLatitude, decimalLongitude, COALESCE(coordinateUncertaintyInMeters, 1000)) AS eeaCellCode, " +
          "  speciesKey, " +
          "  COUNT(*) AS n, " +
          "  MIN(COALESCE(coordinateUncertaintyInMeters, 1000)) AS minCoordinateUncertaintyInMeters " +
          "FROM prod_h.occurrence " +
          "WHERE " +
          "  occurrenceStatus = 'PRESENT' " +
          "  AND speciesKey IS NOT NULL " +

          "  AND NOT stringArrayContains(issue, 'ZERO_COORDINATE', false) " +
          "  AND NOT stringArrayContains(issue, 'COORDINATE_OUT_OF_RANGE', false) " +
          "  AND NOT stringArrayContains(issue, 'COORDINATE_INVALID', false) " +
          "  AND NOT stringArrayContains(issue, 'COUNTRY_COORDINATE_MISMATCH', false) " +

          "  AND (identificationVerificationStatus IS NULL " +
          "    OR NOT (LOWER(identificationVerificationStatus) LIKE '%unverified%' " +
          "         OR LOWER(identificationVerificationStatus) LIKE '%unvalidated%' " +
          "         OR LOWER(identificationVerificationStatus) LIKE '%not able to validate%' " +
          "         OR LOWER(identificationVerificationStatus) LIKE '%control could not be conclusive due to insufficient knowledge%' " +
          "         OR LOWER(identificationVerificationStatus) LIKE '%unconfirmed%' " +
          "         OR LOWER(identificationVerificationStatus) LIKE '%unconfirmed - not reviewed%' " +
          "         OR LOWER(identificationVerificationStatus) LIKE '%validation requested%')) " +

          "  AND countryCode = 'SI' " +
          "  AND year > 1000 " +
          "  AND hasCoordinate " +
          "GROUP BY " +
          "  year, " +
          "  eeaCellCode, " +
          "  speciesKey " +
          "ORDER BY year DESC, eeaCellCode ASC, speciesKey ASC"
      )
      .write()
      .format("csv")
      .option("compression", "none")
      .mode("overwrite")
      // This creates CSV files in HDFS, but the Hive meta-information is incorrect, it declares SequenceFile format.
      // See https://issues.apache.org/jira/browse/SPARK-31799
      // This table can only be read by Spark. (Or at least, cannot be read by Hive.)
      .saveAsTable("matt."+table);

    // Alternatively, save the files to HDFS:
    // .save("hdfs://ha-nn/user/hive/warehouse/matt.db/"+table);
    // And then create the Hive table like this, which can be read by Hive:
    // spark.sql("CREATE EXTERNAL TABLE matt." + table + " " +
    //     "(year INTEGER, eeaCellCode STRING, speciesKey INTEGER, n BIGINT, minCoordinateUncertaintyInMeters DOUBLE) " +
    //     "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' " +
    //     "WITH SERDEPROPERTIES ( " +
    //     "   \"separatorChar\" = \",\" " +
    //     ") " +
    //     "LOCATION 'hdfs://ha-nn/user/hive/warehouse/matt.db/" + table + "' ")
    //   .show();
  }
}
