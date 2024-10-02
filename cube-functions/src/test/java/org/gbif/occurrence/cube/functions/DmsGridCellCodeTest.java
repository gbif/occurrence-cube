package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class DmsGridCellCodeTest {

  DmsGridCellCode dmsgcc = new DmsGridCellCode();

  @Test
  public void basicTest() {
    // 10° 48′ 45.6″
    double lon = 10 + 48/60.0 + 45.6/3600.0;

    // Degree squares (3600 seconds).
    Assert.assertEquals(s("E010°N52°"), dmsgcc.fromCoordinate(3600, 52.0, lon, 0.0));
    Assert.assertEquals(s("E010°S51°"), dmsgcc.fromCoordinate(3600, -52.0, lon, 0.0));
    Assert.assertEquals(s("W010°N52°"), dmsgcc.fromCoordinate(3600, 52.0, -lon, 0.0));
    Assert.assertEquals(s("W010°S51°"), dmsgcc.fromCoordinate(3600, -52.0, -lon, 0.0));

    // 15 minutes squares (900 seconds).
    Assert.assertEquals(s("E010°45′N52°15′"), dmsgcc.fromCoordinate(900, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°45′S52°15′"), dmsgcc.fromCoordinate(900, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°45′N52°15′"), dmsgcc.fromCoordinate(900, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°45′S52°15′"), dmsgcc.fromCoordinate(900, -52.3, -lon, 0.0));

    // 10 minute squares (600 seconds).
    Assert.assertEquals(s("E010°40′N52°10′"), dmsgcc.fromCoordinate(600, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°40′S52°10′"), dmsgcc.fromCoordinate(600, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°40′N52°10′"), dmsgcc.fromCoordinate(600, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°40′S52°10′"), dmsgcc.fromCoordinate(600, -52.3, -lon, 0.0));

    // 5 minute squares (300 seconds).
    Assert.assertEquals(s("E010°45′N52°15′"), dmsgcc.fromCoordinate(300, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°45′S52°15′"), dmsgcc.fromCoordinate(300, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°45′N52°15′"), dmsgcc.fromCoordinate(300, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°45′S52°15′"), dmsgcc.fromCoordinate(300, -52.3, -lon, 0.0));

    // 2½ minutes squares (150 seconds).
    Assert.assertEquals(s("E010°47′30″N52°17′30″"), dmsgcc.fromCoordinate(150, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°47′30″S52°17′30″"), dmsgcc.fromCoordinate(150, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°47′30″N52°17′30″"), dmsgcc.fromCoordinate(150, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°47′30″S52°17′30″"), dmsgcc.fromCoordinate(150, -52.3, -lon, 0.0));

    // 1 minute squares (60 seconds).
    Assert.assertEquals(s("E010°48′N52°18′"), dmsgcc.fromCoordinate(60, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°48′S52°18′"), dmsgcc.fromCoordinate(60, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°48′N52°18′"), dmsgcc.fromCoordinate(60, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°48′S52°18′"), dmsgcc.fromCoordinate(60, -52.3, -lon, 0.0));

    // 30 second squares.
    Assert.assertEquals(s("E010°48′30″N52°18′00″"), dmsgcc.fromCoordinate(30, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°48′30″S52°18′00″"), dmsgcc.fromCoordinate(30, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°48′30″N52°18′00″"), dmsgcc.fromCoordinate(30, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°48′30″S52°18′00″"), dmsgcc.fromCoordinate(30, -52.3, -lon, 0.0));

    // 1 second squares.
    Assert.assertEquals(s("E010°48′45″N52°18′00″"), dmsgcc.fromCoordinate(1, 52.3, lon, 0.0));
    Assert.assertEquals(s("E010°48′45″S52°18′00″"), dmsgcc.fromCoordinate(1, -52.3, lon, 0.0));
    Assert.assertEquals(s("W010°48′45″N52°18′00″"), dmsgcc.fromCoordinate(1, 52.3, -lon, 0.0));
    Assert.assertEquals(s("W010°48′45″S52°18′00″"), dmsgcc.fromCoordinate(1, -52.3, -lon, 0.0));

    // 6 second squares
    Assert.assertEquals(s("E010°48′42″N52°18′00″"), dmsgcc.fromCoordinate(6, 52.3, lon, 0.0));

    // It's not clear where 0°, 0° falls.  I choose the usual upper right quadrant.
    Assert.assertEquals(s("E000°N00°"), dmsgcc.fromCoordinate(3600, 0.0, 0.0, 0.0));
    Assert.assertEquals(s("W000°S00°"), dmsgcc.fromCoordinate(3600, -0.1, -0.1, 0.0));

    // Rounding away from the origin, 0.5°, 0.5° is exactly 'in' the further quadrant, but is then a zero-zero (lower left) for the rest.
    Assert.assertEquals(s("E000°30′N00°30′"), dmsgcc.fromCoordinate(60, 0.5, 0.5, 0.0));
    Assert.assertEquals(s("E000°30′00″N00°30′00″"), dmsgcc.fromCoordinate(1, 0.5, 0.5, 0.0));
    // And so 0.5°, -0.5° is exactly 'in' quadrant A, at the lower right.
    Assert.assertEquals(s("W000°30′N00°30′"), dmsgcc.fromCoordinate(60, 0.5, -0.5, 0.0));
    Assert.assertEquals(s("W000°30′00″N00°30′00″"), dmsgcc.fromCoordinate(1, 0.5, -0.5, 0.0));
    Assert.assertEquals(s("E000°30′S00°30′"), dmsgcc.fromCoordinate(60, -0.5, 0.5, 0.0));
    Assert.assertEquals(s("E000°30′00″S00°30′00″"), dmsgcc.fromCoordinate(1, -0.5, 0.5, 0.0));
    Assert.assertEquals(s("W000°30′S00°30′"), dmsgcc.fromCoordinate(60, -0.5, -0.5, 0.0));
    Assert.assertEquals(s("W000°30′00″S00°30′00″"), dmsgcc.fromCoordinate(1, -0.5, -0.5, 0.0));

    // If puzzling over strange results, double-check for extreme coordinate uncertainty values!
  }

  @Test
  public void illegalArgumentTests() throws Exception {
    // No level
    try {
      dmsgcc.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Negative level
    try {
      dmsgcc.fromCoordinate(-1, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Level not a factor of 3600
    try {
      dmsgcc.fromCoordinate(13, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Beyond 90° / 180°
    try {
      dmsgcc.fromCoordinate(1, 91.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      dmsgcc.fromCoordinate(1, 52.0, 181.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void nullArgumentTest() throws Exception {
    // Missing coordinate or uncertainty
    Assert.assertNull(dmsgcc.fromCoordinate(1, null, 10.0, 0.0));
    Assert.assertNull(dmsgcc.fromCoordinate(1, 52.0, null, 0.0));
    Assert.assertNull(dmsgcc.fromCoordinate(1, 52.0, 10.0, null));
  }

  @Test
  public void beyondExtentTest() throws Exception {
    // Extent is whole world.
  }

  // Strips °′″ symbols which are present for readability.
  private static String s(String s) {
    return s.replaceAll("[°′″]", "");
  }
}
