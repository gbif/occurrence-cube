package org.gbif.occurrence.cube.functions;

import java.io.Serializable;

/**
 * European Economic Area Reference Grid.
 *
 * https://www.eea.europa.eu/data-and-maps/data/eea-reference-grids-2/about-the-eea-reference-grid/eea_reference_grid_v1.pdf/download
 */
public class EeaCellCode extends Etrs89LaeaTransform implements Serializable {

  @Override
  boolean withinBounds(double x, double y) {
    // Limits are the projected bounds from https://epsg.io/3035
    return ! (x < 1_896_628.62 || x > 7_104_179.2 || y < 1_095_703.18 || y > 6_882_401.15);
  }

  String cellCode(int gridSize, double x, double y) {
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

    if (x >= 0) {
      sb.append("E");
    } else {
      x = Math.abs(x);
      sb.append("W");
    }
    // Find the integer part, then round (floor) it with the required divisor
    double xMagnitude = x / Math.pow(10, order);
    double xFloored = t*Math.floor(xMagnitude/t);
    sb.append((int) xFloored);


    if (y >= 0) {
      sb.append("N");
    } else {
      y = Math.abs(y);
      sb.append("S");
    }
    // Find the integer part, then round (floor) it with the required divisor
    double yMagnitude = y / Math.pow(10, order);
    double yFloored = t*Math.floor(yMagnitude/t);
    sb.append((int) yFloored);

    // System.out.println("gridSize "+gridSize + "("+x+","+y+"), order " + order + " t" + t + " grid " + sb + "; " + xMagnitude + " : " + xFloored + ", " + yMagnitude + " : " + yFloored);
    return sb.toString();
  }
}
