package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class EeaCellCodeTest {

  @Test
  public void basicTest() throws Exception {

    EeaCellCode udf = new EeaCellCode();

    Assert.assertEquals("100kmE43N32", udf.fromCoordinate(100_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("10kmE432N321", udf.fromCoordinate(10_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("1kmE4321N3210", udf.fromCoordinate(1_000, 52.0, 10.0, 0.0));
    Assert.assertEquals("250mE432100N321000", udf.fromCoordinate(250, 52.0, 10.0, 0.0));
    Assert.assertEquals("100mE43210N32100", udf.fromCoordinate(100, 52.0, 10.0, 0.0));
    Assert.assertEquals("25mE4321000N3210000", udf.fromCoordinate(25, 52.0, 10.0, 0.0));

    Assert.assertEquals("100kmE32N35", udf.fromCoordinate(100_000,54.176, -6.349, 0.0));
    Assert.assertEquals("1kmE3263N3572", udf.fromCoordinate(1_000,54.176, -6.349, 0.0));
  }

  @Test
  public void failTests() throws Exception {

    EeaCellCode udf = new EeaCellCode();

    try {
      udf.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    Assert.assertNull(udf.fromCoordinate(1_000, null, 10.0, 0.0));
    Assert.assertNull(udf.fromCoordinate(1_000, 52.0, null, 0.0));
    Assert.assertNull(udf.fromCoordinate(1_000, 52.0, 10.0, null));
  }
}

