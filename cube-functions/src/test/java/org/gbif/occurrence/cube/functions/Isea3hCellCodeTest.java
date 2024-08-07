package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class Isea3hCellCodeTest {

  Isea3hCellCode isea3h = new Isea3hCellCode();

  @Test
  public void basicTest() throws Exception {
    Assert.assertEquals("-158282526011250000", isea3h.fromCoordinate(1, 52.0, 10.0, 0.0));
    Assert.assertEquals("-258282526011250000", isea3h.fromCoordinate(2, 52.0, 10.0, 0.0));
    Assert.assertEquals("345573994011250000", isea3h.fromCoordinate(3, 52.0, 10.0, 0.0));
    Assert.assertEquals("452583359004665569", isea3h.fromCoordinate(4, 52.0, 10.0, 0.0));
    Assert.assertEquals("549767897011250000", isea3h.fromCoordinate(5, 52.0, 10.0, 0.0));
    Assert.assertEquals("2251999962009999896", isea3h.fromCoordinate(22, 52.0, 10.0, 0.0));

    // Examples from https://doi.org/10.1080/15230406.2018.1455157
    // Statue of Liberty
    Assert.assertEquals("5340766511074019041", isea3h.fromCoordinate(9, 40.689167, -74.044444, 0.0));
    // Pentagonal cell in the southern ocean
    Assert.assertEquals("-6858282526168750000", isea3h.fromCoordinate(2, -58.282526, -168.75, 0.0));

    // If puzzling over strange results, double-check for extreme coordinate uncertainty values!
  }

  @Test
  public void illegalArgumentTests() throws Exception {
    // No grid size
    try {
      isea3h.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Grid size 0 is not supported.
    try {
      isea3h.fromCoordinate(0, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Grid size 23 is not supported.
    try {
      isea3h.fromCoordinate(23, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // No grid size
    try {
      isea3h.fromCoordinate(null, 52.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

    // Beyond 90° / 180°
    try {
      isea3h.fromCoordinate(1, 91.0, 10.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      isea3h.fromCoordinate(1, 52.0, 181.0, 0.0);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void nullArgumentTest() throws Exception {
    // Missing coordinate or uncertainty
    Assert.assertNull(isea3h.fromCoordinate(1, null, 10.0, 0.0));
    Assert.assertNull(isea3h.fromCoordinate(1, 52.0, null, 0.0));
    Assert.assertNull(isea3h.fromCoordinate(1, 52.0, 10.0, null));
  }
}
