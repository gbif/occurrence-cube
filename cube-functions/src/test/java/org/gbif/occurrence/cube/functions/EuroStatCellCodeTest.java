package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class EuroStatCellCodeTest {

  EuroStatCellCode udf = new EuroStatCellCode();

  @Test
  public void basicTest() throws Exception {
    // Schaan-Vaduz railway station, Lichtenstein
    // 47.168611N, 9.508333E
    Assert.assertEquals("CRS3035RES100000mN2600000E4200000", udf.fromCoordinate(100_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals( "CRS3035RES50000mN2650000E4250000", udf.fromCoordinate(50_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals( "CRS3035RES20000mN2660000E4280000", udf.fromCoordinate(20_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals( "CRS3035RES10000mN2670000E4280000", udf.fromCoordinate(10_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(  "CRS3035RES5000mN2670000E4280000", udf.fromCoordinate(5_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(  "CRS3035RES2000mN2672000E4282000", udf.fromCoordinate(2_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(  "CRS3035RES1000mN2672000E4283000", udf.fromCoordinate(1_000, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(   "CRS3035RES500mN2672500E4283500", udf.fromCoordinate(500, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(   "CRS3035RES200mN2672800E4283600", udf.fromCoordinate(200, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(   "CRS3035RES100mN2672900E4283600", udf.fromCoordinate(100, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(    "CRS3035RES50mN2672900E4283650", udf.fromCoordinate(50, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(    "CRS3035RES20mN2672900E4283680", udf.fromCoordinate(20, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(    "CRS3035RES10mN2672900E4283690", udf.fromCoordinate(10, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(     "CRS3035RES5mN2672905E4283690", udf.fromCoordinate(5, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(     "CRS3035RES2mN2672906E4283694", udf.fromCoordinate(2, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(     "CRS3035RES1mN2672906E4283694", udf.fromCoordinate(1, 47.168611, 9.508333, 0.0));

    Assert.assertEquals(   "CRS3035RES250mN2672750E4283500", udf.fromCoordinate(250, 47.168611, 9.508333, 0.0));
    Assert.assertEquals(    "CRS3035RES25mN2672900E4283675", udf.fromCoordinate(25, 47.168611, 9.508333, 0.0));

    // Any integer grid size is supported, but this isn't documented
    Assert.assertEquals(  "CRS3035RES1303mN2672453E4282961", udf.fromCoordinate(1_303, 47.168611, 9.508333, 0.0));

    // Example (Carlisle, UK)
    Assert.assertEquals("CRS3035RES100000mN3600000E3400000", udf.fromCoordinate(100_000,54.894722, -2.936389, 0.0));
    Assert.assertEquals(  "CRS3035RES1000mN3606000E3495000", udf.fromCoordinate(1_000,54.894722, -2.936389, 0.0));

    // Mayotte
    Assert.assertEquals("CRS3035RES100000mN-2800000E8700000", udf.fromCoordinate(100_000,-12.843056, 45.138333, 0.0));

    // Le Robert, Martinique
    Assert.assertEquals(  "CRS3035RES5000mN2500000E-2665000", udf.fromCoordinate(5_000,14.6792, -60.9403, 0.0));

    // If puzzling over strange results, double-check for extreme coordinate uncertainty values!
  }

  @Test
  public void illegalArgumentTests() throws Exception {
    // No grid size
    try {
      udf.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Zero grid size
    try {
      udf.fromCoordinate(0, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Size ≥ 1Mm
    try {
      udf.fromCoordinate(1_000_000, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Beyond 90° / 180°
    try {
      udf.fromCoordinate(1, 91.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      udf.fromCoordinate(1, 52.0, 181.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void nullArgumentTest() throws Exception {
    // Missing coordinate or uncertainty
    Assert.assertNull(udf.fromCoordinate(1, null, 10.0, 0.0));
    Assert.assertNull(udf.fromCoordinate(1, 52.0, null, 0.0));
    Assert.assertNull(udf.fromCoordinate(1, 52.0, 10.0, null));
  }

  @Test
  public void beyondExtentTest() throws Exception {
    for (double l = -180; l < 180; l+=10) {
      udf.fromCoordinate(100_000, 50.0, l, 0.0);
    }

    udf.fromCoordinate(100_000, 32.88, -16.1, 0.0);
    udf.fromCoordinate(100_000, 84.73, 40.18, 0.0);
  }
}

