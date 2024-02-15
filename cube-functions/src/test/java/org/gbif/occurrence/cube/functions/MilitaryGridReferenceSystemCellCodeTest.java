package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class MilitaryGridReferenceSystemCellCodeTest {

  MilitaryGridReferenceSystemCellCode mgrs = new MilitaryGridReferenceSystemCellCode();

  @Test
  public void basicTest() throws Exception {
    // Values checked with https://www.legallandconverter.com/p50.html
    Assert.assertEquals("32U", mgrs.fromCoordinate(0, 52.0, 10.0, 0.0));
    Assert.assertEquals("32UNC", mgrs.fromCoordinate(100_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("32UNC66", mgrs.fromCoordinate(10_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("32UNC6861", mgrs.fromCoordinate(1_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("32UNC686615", mgrs.fromCoordinate(100, 52.0, 10.0, 0.0));
    Assert.assertEquals("32UNC68646151", mgrs.fromCoordinate(10, 52.0, 10.0, 0.0));
    Assert.assertEquals("32UNC6864961510", mgrs.fromCoordinate(1, 52.0, 10.0, 0.0));

    Assert.assertEquals("29FMC3135038489", mgrs.fromCoordinate(1, -52.0, -10.0, 0.0));

    Assert.assertEquals("31NAA6602100000", mgrs.fromCoordinate(1, 0.0, 0.0, 0.0));

    Assert.assertEquals("33TVL", mgrs.fromCoordinate(100_000, 45.8631, 14.5367, 1000.0));
  }

  @Test
  public void illegalArgumentTests() throws Exception {
    // No grid size
    try {
      mgrs.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Beyond 90° / 180°
    try {
      mgrs.fromCoordinate(1, 91.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      mgrs.fromCoordinate(1, 52.0, 181.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void nullArgumentTest() throws Exception {
    // Missing coordinate or uncertainty
    Assert.assertNull(mgrs.fromCoordinate(1, null, 10.0, 0.0));
    Assert.assertNull(mgrs.fromCoordinate(1, 52.0, null, 0.0));
    Assert.assertNull(mgrs.fromCoordinate(1, 52.0, 10.0, null));
  }

  @Test
  public void beyondExtentTest() throws Exception {
    // Polar regions are not supported.
    // https://github.com/ngageoint/mgrs-java/issues/2 suggests they could be.
    Assert.assertNull(mgrs.fromCoordinate(1, Math.nextUp(84.0), 0.0, 0.0));
    Assert.assertNull(mgrs.fromCoordinate(1, Math.nextDown(-80.0), 0.0, 0.0));
  }
}

