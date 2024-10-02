package org.gbif.occurrence.cube.functions;

import org.gbif.occurrence.cube.functions.ExtendedQuarterDegreeGridCellCode;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geopkg.GeoPkgDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Generate a Geopackage shapefile containing Extended Quarter-Degree Grid Cells covering the whole globe,
 * at the specified level.
 */
public class GridGenerator {
  static ExtendedQuarterDegreeGridCellCode eqdgcc = new ExtendedQuarterDegreeGridCellCode();
  static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();

    int level = 2;
    if (args.length == 1) {
      level = Integer.parseInt(args[0]);
    }
    System.out.println("Generating level "+level);

    File file = new File("EQDGC-Level-"+level+".gpkg");
    file.delete();

    HashMap<String, Object> map = new HashMap<>();
    map.put(GeoPkgDataStoreFactory.DBTYPE.key, GeoPkgDataStoreFactory.DBTYPE.sample);
    map.put(GeoPkgDataStoreFactory.DATABASE.key, file.toString());
    map.put(JDBCDataStoreFactory.BATCH_INSERT_SIZE.key, 10_000);

    System.out.println("writing to " + file);
    DataStore dataStore = DataStoreFinder.getDataStore(map);

    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    String layerName = "EQDGC Level "+level;
    builder.setName(layerName);
    builder.setSRS("EPSG:4326");
    builder.add("geom", Polygon.class);
    builder.add("cellCode", String.class);
    SimpleFeatureType featureType = builder.buildFeatureType();
    DefaultTransaction transaction = new DefaultTransaction("transaction");
    dataStore.createSchema(featureType);

    SimpleFeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource(layerName);
    featureStore.setTransaction(transaction);

    double s = 1.0/(Math.pow(2, level));

    int c = 0;

    List<SimpleFeature> features = new ArrayList<>();

    for (double x = -180; x < 180; x += s) {
      for (double y = -90; y < 90; y += s) {
        if (Math.abs(x) <= 1 && Math.abs(y) <= 1) {
          System.out.println("lon=" + x + ", lat=" + y);
        }
        Coordinate[] coords = new Coordinate[]{
          new Coordinate(x, y),
          new Coordinate(x + s, y),
          new Coordinate(x + s, y + s),
          new Coordinate(x,y + s),
          new Coordinate(x, y)
        };
        String cellCode = eqdgcc.extendedQuarterDegreeGridCellCode(level, y + s/2, x + s/2);
        Polygon polygon = geometryFactory.createPolygon(coords);

        Object[] attributes = new Object[] { polygon, cellCode };
        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, attributes, null);
        features.add(feature);

        if (c % 10_000 == 0) {
          System.out.println("Writing features to feature " + c);
          SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);

          try {
            featureStore.addFeatures(collection);
            transaction.commit();
          } catch (Exception problem) {
            problem.printStackTrace();
            transaction.rollback();
          } finally {
            transaction.close();
          }

          features.clear();
          transaction = new DefaultTransaction("transaction");
          featureStore.setTransaction(transaction);
        }

        c++;
      }
    }

    // Write final list of features
    System.out.println("Writing features to feature " + c);
    SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
    try {
      featureStore.addFeatures(collection);
      transaction.commit();
    } catch (Exception problem) {
      problem.printStackTrace();
      transaction.rollback();
    } finally {
      transaction.close();
    }

    System.out.println("Wrote " + c + " features");

    dataStore.dispose();

    long end = System.currentTimeMillis();
    System.out.println("GPKG created in " + (end - start) / 1000.0 + " seconds.");
  }
}
