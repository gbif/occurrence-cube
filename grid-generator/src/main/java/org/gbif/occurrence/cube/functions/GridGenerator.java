package org.gbif.occurrence.cube.functions;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Generate a Geopackage shapefile containing grid cells covering the whole globe,
 * at the specified level, with the provided gridding function.
 */
public abstract class GridGenerator {
  static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

  abstract String gridCode(int level, double lat, double lon);

  abstract String layerName(int level);

  abstract double step(int level);

  void makeGrid(int level, File file) throws IOException {
    long start = System.currentTimeMillis();
    System.out.println("Generating level " + level);

    file.delete();

    HashMap<String, Object> map = new HashMap<>();
    map.put(GeoPkgDataStoreFactory.DBTYPE.key, GeoPkgDataStoreFactory.DBTYPE.sample);
    map.put(GeoPkgDataStoreFactory.DATABASE.key, file.toString());
    map.put(JDBCDataStoreFactory.BATCH_INSERT_SIZE.key, 10_000);

    System.out.println("Writing to " + file);
    DataStore dataStore = DataStoreFinder.getDataStore(map);

    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    String layerName = layerName(level);
    builder.setName(layerName);
    builder.setSRS("EPSG:4326");
    builder.add("geom", Polygon.class);
    builder.add("cellCode", String.class);
    SimpleFeatureType featureType = builder.buildFeatureType();
    DefaultTransaction transaction = new DefaultTransaction("transaction");
    dataStore.createSchema(featureType);

    SimpleFeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource(layerName);
    featureStore.setTransaction(transaction);

    double s = step(level);

    int c = 0;

    List<SimpleFeature> features = new ArrayList<>();
    BigDecimal bds = new BigDecimal(step(level));
    BigDecimal bd180 = new BigDecimal(180).subtract(bds);
    BigDecimal bd90 = new BigDecimal(90).subtract(bds);
    System.out.println("Limit is "+bd180+","+bd90);
    for (BigDecimal bdx = new BigDecimal(-180); bdx.compareTo(bd180) < 1; bdx = bdx.add(bds)) {
      for (BigDecimal bdy = new BigDecimal(-90); bdy.compareTo(bd90) < 1; bdy = bdy.add(bds)) {
        double x = bdx.doubleValue();
        double y = bdy.doubleValue();

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
        try {
          String cellCode = gridCode(level, y + s / 2, x + s / 2);
          Polygon polygon = geometryFactory.createPolygon(coords);

          Object[] attributes = new Object[]{polygon, cellCode};
          SimpleFeature feature = SimpleFeatureBuilder.build(featureType, attributes, null);
          features.add(feature);

          if (c % 10_000 == 0 || c < 10) {
            System.out.println("Writing features to feature " + c + " at (" + x + "," + y + ")");
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
        } catch (IllegalArgumentException e) {
          System.err.println("Error at " + level + ", " + (y + s / 2) + ", " + (x + s / 2));
          throw e;
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
