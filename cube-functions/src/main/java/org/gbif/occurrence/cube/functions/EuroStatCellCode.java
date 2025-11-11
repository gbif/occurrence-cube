package org.gbif.occurrence.cube.functions;

import java.io.Serializable;

/**
 * EuroStat Grid Cell code from the INSPIRE Data Specification on Statistical Units – Technical Guidelines
 *
 * https://knowledge-base.inspire.ec.europa.eu/publications/inspire-data-specification-statistical-units-technical-guidelines_en
 */
public class EuroStatCellCode extends Etrs89LaeaTransform implements Serializable {

  @Override
  boolean withinBounds(double x, double y) {
    return true;
  }

  String cellCode(int gridSize, double x, double y) {
    // The cell code is built according to the following pattern:
    // CRS<EPSGcode>*RES*(<size>mN<northing>E<easting>)|(<d>-<m>-<s>dmsLON<d>-<m>-<s>LAT<d>-<m>-<s>)
    // This code is composed of:
    // A coordinate reference system part, represented by the word CRS, followed by the EPSG code.
    // A resolution and position part, which can be:
    // if the coordinate reference system is projected, the word RES followed by the grid resolution in meters and the letter m. Then, the letter N followed by the northing value in meters, and the letter E followed by the easting value in meters too,
    // if the coordinate reference system is not projected, the word RES followed by the grid resolution in degree-minute-second, followed by the word dms. Then the word LON followed by the longitude value in degree-minute-second, and word LAT followed by the latitude value in degree-minute-second.

    // Find order (number of zeros) of the gridSize
    int order = 0;
    int t = gridSize;
    while (t % 10 == 0 && t != 0) {
      t /= 10;
      order++;
    }

    StringBuilder sb = new StringBuilder("CRS3035RES");
    sb.append(gridSize);
    sb.append("m");

    // Find the integer part, then round (floor) it with the required divisor
    sb.append("N");
    double divisor = Math.pow(10, order);
    double yMagnitude = y / divisor;
    double yFloored = t*Math.floor(yMagnitude/t);
    sb.append((int) (yFloored*divisor));

    // Find the integer part, then round (floor) it with the required divisor
    sb.append("E");
    double xMagnitude = x / divisor;
    double xFloored = t*Math.floor(xMagnitude/t);
    sb.append((int) (xFloored*divisor));

    // System.out.println("gridSize "+gridSize + "("+x+","+y+"), order " + order + " t" + t + " grid " + sb + "; " + xMagnitude + " : " + xFloored + ", " + yMagnitude + " : " + yFloored);
    return sb.toString();
  }
}
