package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class DmsGridCellCodeTest {

  DmsGridCellCode dmsgcc = new DmsGridCellCode();

  @Test
  public void basicTest() {
    // 10° 48′ 45.6″ = 10.81266666666666…
    double lon = 10 + 48/60.0 + 45.6/3600.0;

    // 52° 18′ 0.0″ = 52.3
    double lat = 52 + 18/60.0;

    // CRS4326RES0-10-0dmsLON45-20-0LAT4-50-0

    // Around Null Island
    Assert.assertEquals("CRS4326RES1-0-0LON0-0-0LAT0-0-0", dmsgcc.fromCoordinate(3600, 0.1, 0.1, 0.0));
    Assert.assertEquals("CRS4326RES1-0-0LON-1-0-0LAT-1-0-0", dmsgcc.fromCoordinate(3600, -0.1, -0.1, 0.0));

    Assert.assertEquals("CRS4326RES1-0-0LON-1-0-0LAT-1-0-0", dmsgcc.fromCoordinate(3600, -0.9, -0.9, 0.0));
    Assert.assertEquals("CRS4326RES1-0-0LON-2-0-0LAT-2-0-0", dmsgcc.fromCoordinate(3600, -1.1, -1.1, 0.0));

    Assert.assertEquals("CRS4326RES0-0-1LON0-0-3LAT0-0-3", dmsgcc.fromCoordinate(1, 0.001, 0.001, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON-0-0-4LAT-0-0-4", dmsgcc.fromCoordinate(1, -0.001, -0.001, 0.0));

    Assert.assertEquals("CRS4326RES0-0-1LON0-6-39LAT0-6-39", dmsgcc.fromCoordinate(1, 0.111, 0.111, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON-0-6-40LAT-0-6-40", dmsgcc.fromCoordinate(1, -0.111, -0.111, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON-1-6-40LAT-1-6-40", dmsgcc.fromCoordinate(1, -1.111, -1.111, 0.0));

    // Move across zero in whole degrees
    Assert.assertEquals("CRS4326RES1-0-0LON10-0-0LAT1-0-0", dmsgcc.fromCoordinate(3600, 1.0, lon, 0.0));
    Assert.assertEquals("CRS4326RES1-0-0LON10-0-0LAT0-0-0", dmsgcc.fromCoordinate(3600, 0.0, lon, 0.0));
    Assert.assertEquals("CRS4326RES1-0-0LON10-0-0LAT-1-0-0", dmsgcc.fromCoordinate(3600, -1.0, lon, 0.0));
    Assert.assertEquals("CRS4326RES1-0-0LON10-0-0LAT-2-0-0", dmsgcc.fromCoordinate(3600, -2.0, lon, 0.0));

    // Move across zero in whole minutes
    Assert.assertEquals("CRS4326RES0-1-0LON10-48-0LAT0-3-0", dmsgcc.fromCoordinate(60, 0.05, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-1-0LON10-48-0LAT0-0-0", dmsgcc.fromCoordinate(60, 0.0, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-1-0LON10-48-0LAT-0-3-0", dmsgcc.fromCoordinate(60, -0.05, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-1-0LON10-48-0LAT-0-6-0", dmsgcc.fromCoordinate(60, -0.10, lon, 0.0));

    // Move across zero in whole seconds
    Assert.assertEquals("CRS4326RES0-0-1LON10-48-45LAT0-0-9", dmsgcc.fromCoordinate(1, 0.0025, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON10-48-45LAT0-0-0", dmsgcc.fromCoordinate(1, 0.0, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON10-48-45LAT-0-0-9", dmsgcc.fromCoordinate(1, -0.0025, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON10-48-45LAT-0-0-18", dmsgcc.fromCoordinate(1, -0.0050, lon, 0.0));

    // Degree squares (3600 seconds).
    Assert.assertEquals("CRS4326RES1-0-0LON10-0-0LAT52-0-0", dmsgcc.fromCoordinate(3600, 52.0, lon, 0.0));
    Assert.assertEquals("CRS4326RES1-0-0LON-11-0-0LAT-52-0-0", dmsgcc.fromCoordinate(3600, -52.0, -lon, 0.0));

    // 15 minutes squares (900 seconds).
    Assert.assertEquals("CRS4326RES0-15-0LON10-45-0LAT52-15-0", dmsgcc.fromCoordinate(900, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-15-0LON-11-0-0LAT-52-30-0", dmsgcc.fromCoordinate(900, -lat, -lon, 0.0));

    // 10 minute squares (600 seconds).
    Assert.assertEquals("CRS4326RES0-10-0LON10-40-0LAT52-10-0", dmsgcc.fromCoordinate(600, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-10-0LON-10-50-0LAT-52-20-0", dmsgcc.fromCoordinate(600, -lat, -lon, 0.0));

    // 5 minute squares (300 seconds).
    Assert.assertEquals("CRS4326RES0-5-0LON10-45-0LAT52-15-0", dmsgcc.fromCoordinate(300, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-5-0LON-10-50-0LAT-52-20-0", dmsgcc.fromCoordinate(300, -lat, -lon, 0.0));

    // 2½ minutes squares (150 seconds).
    Assert.assertEquals("CRS4326RES0-2-30LON10-47-30LAT52-17-30", dmsgcc.fromCoordinate(150, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-2-30LON-10-50-0LAT-52-20-0", dmsgcc.fromCoordinate(150, -lat, -lon, 0.0));

    // 1 minute squares (60 seconds).
    // Due to floating point arithmetic, this is 52-17-0 when it should be 52-18-0, similarly for the negative.
    Assert.assertEquals("CRS4326RES0-1-0LON10-48-0LAT52-17-0", dmsgcc.fromCoordinate(60, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-1-0LON-10-49-0LAT-52-18-0", dmsgcc.fromCoordinate(60, -lat, -lon, 0.0));

    // 30 second squares.
    // Due to floating point arithmetic, this is 52-17-30 when it should be 52-18-0, similarly for the negative.
    Assert.assertEquals("CRS4326RES0-0-30LON10-48-30LAT52-17-30", dmsgcc.fromCoordinate(30, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-0-30LON-10-49-0LAT-52-18-0", dmsgcc.fromCoordinate(30, -lat, -lon, 0.0));

    // 1 second squares.
    // Due to floating point arithmetic, this is 52-17-59 when it should be 52-18-0, similarly for the negative.
    Assert.assertEquals("CRS4326RES0-0-1LON10-48-45LAT52-17-59", dmsgcc.fromCoordinate(1, lat, lon, 0.0));
    Assert.assertEquals("CRS4326RES0-0-1LON-10-48-46LAT-52-18-0", dmsgcc.fromCoordinate(1, -lat, -lon, 0.0));

    // 6 second squares
    Assert.assertEquals("CRS4326RES0-0-6LON10-48-42LAT52-17-54", dmsgcc.fromCoordinate(6, lat, lon, 0.0));

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
}
