package org.gbif.occurrence.cube.functions;

import java.io.File;
import java.io.IOException;

/**
 * Generate a Geopackage shapefile containing Degree-Minute-Second Grid Cells covering the whole globe,
 * at the specified level.
 */
public class DmsGridGenerator extends GridGenerator {
  static DmsGridCellCode dmsgcc = new DmsGridCellCode();

  public static void main(String[] args) throws IOException {

    GridGenerator gg = new DmsGridGenerator();

    int[] levels = {3600, 1800, 900, 600, 300, 150, 60, 30};
    if (args.length == 1) {
      levels = new int[] {Integer.parseInt(args[0])};
    }

    for (int level : levels) {
      File file = new File("/extra/DMS-" + level + "-second.gpkg");
      gg.makeGrid(level, file);
    }
  }

  public String gridCode(int level, double lat, double lon) {
    return dmsgcc.fromCoordinate(level, lat, lon, 0.0);
  }

  String layerName(int level) {
    return level + " second grid";
  }

  double step(int level) {
    return level/3600.0;
  }
}
