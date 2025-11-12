package org.gbif.occurrence.cube.functions;

import java.io.File;
import java.io.IOException;

/**
 * Generate a Geopackage shapefile containing Extended Quarter-Degree Grid Cells covering the whole globe,
 * at the specified level.
 */
public class ExtendedQuarterDegreeGridGenerator extends GridGenerator {
  ExtendedQuarterDegreeGridCellCode eqdgcc = new ExtendedQuarterDegreeGridCellCode();

  public static void main(String[] args) throws IOException {

    GridGenerator gg = new ExtendedQuarterDegreeGridGenerator();

    int[] levels = {0, 1, 2, 3, 4, 5, 6, 7};
    if (args.length == 1) {
      levels = new int[] {Integer.parseInt(args[0])};
    }

    for (int level : levels) {
      File file = new File("/extra/EQDGC-Level-" + level + ".gpkg");
      gg.makeGrid(level, file);
    }
  }

  public String gridCode(int level, double lat, double lon) {
    return eqdgcc.fromCoordinate(level, lat, lon, 0.0);
  }

  String layerName(int level) {
    return "EQDGC Level " + level;
  }

  double step(int level) {
    return 1.0/(Math.pow(2, level));
  }
}
