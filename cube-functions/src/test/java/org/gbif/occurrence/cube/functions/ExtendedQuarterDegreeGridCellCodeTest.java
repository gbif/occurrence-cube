package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class ExtendedQuarterDegreeGridCellCodeTest {

  ExtendedQuarterDegreeGridCellCode eqdgcc = new ExtendedQuarterDegreeGridCellCode();

  @Test
  public void basicTest() {
    Assert.assertEquals("E010N52", eqdgcc.fromCoordinate(0, 52.0, 10.0, 0.0));
    Assert.assertEquals("E010S52", eqdgcc.fromCoordinate(0, -52.0, 10.0, 0.0));
    Assert.assertEquals("W010N52", eqdgcc.fromCoordinate(0, 52.0, -10.0, 0.0));
    Assert.assertEquals("W010S52", eqdgcc.fromCoordinate(0, -52.0, -10.0, 0.0));

    Assert.assertEquals("E010N52C", eqdgcc.fromCoordinate(1, 52.3, 10.3, 0.0));
    Assert.assertEquals("E010S52A", eqdgcc.fromCoordinate(1, -52.3, 10.3, 0.0));
    Assert.assertEquals("W010N52D", eqdgcc.fromCoordinate(1, 52.3, -10.3, 0.0));
    Assert.assertEquals("W010S52B", eqdgcc.fromCoordinate(1, -52.3, -10.3, 0.0));

    Assert.assertEquals("E010N52CB", eqdgcc.fromCoordinate(2, 52.3, 10.3, 0.0));
    Assert.assertEquals("E010S52AD", eqdgcc.fromCoordinate(2, -52.3, 10.3, 0.0));
    Assert.assertEquals("W010N52DA", eqdgcc.fromCoordinate(2, 52.3, -10.3, 0.0));
    Assert.assertEquals("W010S52BC", eqdgcc.fromCoordinate(2, -52.3, -10.3, 0.0));

    Assert.assertEquals("E010N52CBC", eqdgcc.fromCoordinate(3, 52.3, 10.3, 0.0));
    Assert.assertEquals("E010S52ADA", eqdgcc.fromCoordinate(3, -52.3, 10.3, 0.0));
    Assert.assertEquals("W010N52DAD", eqdgcc.fromCoordinate(3, 52.3, -10.3, 0.0));
    Assert.assertEquals("W010S52BCB", eqdgcc.fromCoordinate(3, -52.3, -10.3, 0.0));

    Assert.assertEquals("E010N52CBCCBB", eqdgcc.fromCoordinate(6, 52.3, 10.3, 0.0));

    // It's not clear where 0°, 0° falls.  I choose the usual upper right quadrant.
    Assert.assertEquals("E000N00", eqdgcc.fromCoordinate(0, 0.0, 0.0, 0.0));
    Assert.assertEquals("W000S00", eqdgcc.fromCoordinate(0, -0.5, -0.5, 0.0));
    // Following the pattern, 0.5°, 0.5° is exactly 'in' the further quadrant, but is then a zero-zero (lower left) for the rest.
    Assert.assertEquals("E000N00B", eqdgcc.fromCoordinate(1, 0.5, 0.5, 0.0));
    Assert.assertEquals("W000S00C", eqdgcc.fromCoordinate(1, -0.5, -0.5, 0.0));
    Assert.assertEquals("E000N00BCCCCC", eqdgcc.fromCoordinate(6, 0.5, 0.5, 0.0));
    Assert.assertEquals("W000S00CBBBBB", eqdgcc.fromCoordinate(6, -0.5, -0.5, 0.0));

    Assert.assertEquals("E014N46CA", eqdgcc.fromCoordinate(2, 46.377106, 14.033383, 1000.0));

    // If puzzling over strange results, double-check for extreme coordinate uncertainty values!
  }

  @Test
  public void illegalArgumentTests() throws Exception {
    // No level
    try {
      eqdgcc.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Beyond 90° / 180°
    try {
      eqdgcc.fromCoordinate(1, 91.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      eqdgcc.fromCoordinate(1, 52.0, 181.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void nullArgumentTest() throws Exception {
    // Missing coordinate or uncertainty
    Assert.assertNull(eqdgcc.fromCoordinate(1, null, 10.0, 0.0));
    Assert.assertNull(eqdgcc.fromCoordinate(1, 52.0, null, 0.0));
    Assert.assertNull(eqdgcc.fromCoordinate(1, 52.0, 10.0, null));
  }

  @Test
  public void beyondExtentTest() throws Exception {
    // Extent is whole world.
  }
}

