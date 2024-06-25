package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class EeaCellCodeTest {

  EeaCellCode udf = new EeaCellCode();

  @Test
  public void basicTest() throws Exception {
    Assert.assertEquals("100kmE43N32", udf.fromCoordinate(100_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("10kmE432N321", udf.fromCoordinate(10_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("1kmE4321N3210", udf.fromCoordinate(1_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("250mE432100N321000", udf.fromCoordinate(250, 52.0, 10.0, 0.0));
    Assert.assertEquals("100mE43210N32100", udf.fromCoordinate(100, 52.0, 10.0, 0.0));
    Assert.assertEquals("25mE4321000N3210000", udf.fromCoordinate(25, 52.0, 10.0, 0.0));

    Assert.assertEquals("100kmE32N35", udf.fromCoordinate(100_000,54.176, -6.349, 0.0));
    Assert.assertEquals("1kmE3263N3572", udf.fromCoordinate(1_000,54.176, -6.349, 0.0));

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
    // Polar regions are not supported.
    // https://github.com/ngageoint/udf-java/issues/2 suggests they could be.
    Assert.assertNull(udf.fromCoordinate(1, 29.3235947, -14.9412539, 0.0));
    Assert.assertNull(udf.fromCoordinate(1, 65.9889821, 95.3766984, 0.0));

    for (double l = -180; l < 180; l+=10) {
      udf.fromCoordinate(100_000, 50.0, l, 0.0);

    }

    udf.fromCoordinate(100_000, 32.88, -16.1, 0.0);
    udf.fromCoordinate(100_000, 84.73, 40.18, 0.0);

  }
}

