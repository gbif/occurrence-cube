package org.gbif.occurrence;

import org.apache.spark.sql.SparkSession;
import org.gbif.occurrence.spark.udf.UDFS;

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

//    String table = "datacube_europe_lynx_lynx_1km_b";
//    String table = "datacube_europe_carduelis_citrinella_1km";
//    String table = "datacube_europe_reynoutria_japonica_1km";
    String table = "datacube_slovenia_1km";

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

          // User query:
          //"  AND countryCode = 'SI' " +

          // Netherlands farm bird query.
//          "  AND year = 2018 " +
//          "  AND decimalLongitude >= 5.5831989141242966 " +
//          "  AND decimalLongitude <= 6.4086515407429623 " +
//          "  AND decimalLatitude >= 52.5917375949509562 " +
//          "  AND decimalLatitude <= 52.8070852699905871 " +
//          "  AND speciesKey IN (8077224, 8332393, 2490266, 2497266, 2494686, 2482513, 2474156, 2491534, 9616058, 2481819, 7788295, 2493220, 9515886, 2481685, 7634625, 9701857, 2481792, 5231198, 2473958, 8250742, 2492526, 2495708, 9809229, 2492943, 2481714, 2490774, 2480242) " +

//          "  AND year >= 2000 " +
//          "  AND coordinateUncertaintyInMeters IS NOT NULL AND coordinateUncertaintyInMeters <= 1000 " +
//          "  AND continent = 'EUROPE' " +
//          "  AND speciesKey = 2435240 " + // Lynx lynx (Linnaeus, 1758)
//          "  AND speciesKey = 2494632 " + // Carduelis citrinella (Pallas, 1764)
//          "  AND speciesKey = 2889173 " + // Reynoutria japonica Houtt.

          // End user query
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
      //.saveAsTable("matt."+table);

    // Alternatively, save the files to HDFS:
     .save("hdfs://ha-nn/user/hive/warehouse/matt.db/"+table);
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
