package org.gbif.occurrence.cube.grids;

import java.io.File;
import java.io.IOException;

/**
 * Generate a Geopackage shapefile containing EEA RG Grid Cells covering Europe,
 * at the specified level.
 */
public class EeaGridGenerator extends GridGenerator {
  static EeaCellCodeGeopackage31 eeacc = new EeaCellCodeGeopackage31();

  EeaGridGenerator(String srs, int startX, int startY, int endX, int endY) {
    super(srs, startX, startY, endX, endY);
  }

  public static void main(String[] args) throws IOException {

    // Bounds for the generated grid
    EeaGridGenerator gg = new EeaGridGenerator("EPSG:3035", -200_000, 700_000, 8_400_001, 7_500_001);

    // Levels to generate
    int[] levels = {100_000, 50_000, 10_000, 5_000, 2_000, 1_000};
    if (args.length == 1) {
      levels = new int[] {Integer.parseInt(args[0])};
    }

    // Destination to output file
    for (int level : levels) {
      File file = new File("/extra/EEA-Reference-Grid-" + gg.name(level) + ".gpkg");
      gg.makeGrid(level, file);
    }
  }

  public String gridCode(int level, double lat, double lon) {
    try {
      return eeacc.cellCode(level, lon, lat);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  String layerName(int level) {
    return "EEA-" + name(level) + "-grid";
  }

  double step(int level) {
    return level;
  }

  String name(int gridSize) {
    // Find order (number of zeros) of the gridSize
    int order = 0;
    int t = gridSize;
    while (t % 10 == 0 && t != 0) {
      t /= 10;
      order++;
    }

    // Format the grid size using m or km.
    int o = order;
    StringBuilder sb = new StringBuilder(String.valueOf(t));
    if (o % 3 != 0) {
      sb.append('0');
      o--;
    }
    if (o % 3 != 0) {
      sb.append('0');
      o--;
    }
    if (o == 3) {
      sb.append('k');
    }
    sb.append('m');
    return sb.toString();
  }
}
