package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;

public class RandomizeCoordinateTest {

  @Test
  public void basicTest() throws Exception {
    Assert.assertEquals(new Point2D.Double(12.0, 55.5), RandomizeCoordinate.moveCoordinate(new Point2D.Double(12.0, 55.5), 0.0));

    // Check the shifted points are distributed roughly evenly around the original point,
    // i.e. a quarter in each quadrant.
    int[] quads = new int[4];

    for (int i = 0; i < 40_000; i++) {
      Point2D point = RandomizeCoordinate.moveCoordinate(new Point2D.Double(0.0, 0.0), 110_000.0);

      double x = point.getX();
      double y = point.getY();

      // Also check they are not further than expected away
      Assert.assertTrue(Math.sqrt(x*x + y*y) < 1.0);

      quads[ (x>0?1:0) + (y>0?2:0) ]++;
    }

    for (int q : quads) {
      Assert.assertTrue(q >= 9000 && q <= 11000);
    }
  }
}

