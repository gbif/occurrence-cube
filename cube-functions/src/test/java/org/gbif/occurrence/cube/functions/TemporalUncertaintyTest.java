package org.gbif.occurrence.cube.functions;

import org.junit.Assert;
import org.junit.Test;

public class TemporalUncertaintyTest {

  TemporalUncertainty tu = new TemporalUncertainty();

  @Test
  public void eventDateTest() throws Exception {
    long HOUR = 60 * 60;
    long DAY = HOUR * 24;

    // Milliseconds are rounded to seconds
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32.456Z", null));
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32.456", null));
    // Seconds
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32Z", null));
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32", null));
    // Minutes, or zero seconds
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01:00Z", null));
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01:00", null));
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01Z", null));
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01", null));
    // Hours, or zero minutes and seconds
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00:00Z", null));
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00:00", null));
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00Z", null));
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00", null));
    // Days
    Assert.assertEquals((Long) (DAY), tu.fromEventDateAndEventTime("2021-03-21", null));
    // Months
    Assert.assertEquals((Long) (DAY * 28L), tu.fromEventDateAndEventTime("2021-02", null));
    Assert.assertEquals((Long) (DAY * 29L), tu.fromEventDateAndEventTime("2024-02", null)); // Leap year
    // Years
    Assert.assertEquals((Long) (DAY * 365L), tu.fromEventDateAndEventTime("2021", null));
    Assert.assertEquals((Long) (DAY * 366L), tu.fromEventDateAndEventTime("2024", null)); // Leap year

    Assert.assertEquals((Long) (DAY * 3), tu.fromEventDateAndEventTime("2021-03-21/2021-03-23", null));
    Assert.assertEquals((Long) (DAY * (31 + 30)), tu.fromEventDateAndEventTime("2021-03/2021-04", null));
    Assert.assertEquals((Long) (DAY * 365 * 2), tu.fromEventDateAndEventTime("2021/2022", null));
    Assert.assertEquals((Long) (DAY * (365 * 3 + 1)), tu.fromEventDateAndEventTime("2022/2024", null)); // Leap year
  }

  @Test
  public void eventDateAndEventTimeTest() throws Exception {
    // Only the one marked XX is different, as the time is ignored unless it's a date without one.

    long HOUR = 60 * 60;
    long DAY = HOUR * 24;

    // Milliseconds are rounded to seconds
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32.456Z", "15:01"));
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32.456", "15:01"));
    // Seconds
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32Z", "15:01"));
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21T15:01:32", "15:01"));
    // Minutes, or zero seconds
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01:00Z", "15:01:01"));
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01:00", "15:01:01"));
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01Z", "15:01:01"));
    Assert.assertEquals((Long) (60L), tu.fromEventDateAndEventTime("2021-03-21T15:01", "15:01:01"));
    // Hours, or zero minutes and seconds
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00:00Z", "15:01:01"));
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00:00", "15:01:01"));
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00Z", "15:01:01"));
    Assert.assertEquals((Long) (HOUR), tu.fromEventDateAndEventTime("2021-03-21T15:00", "15:01:01"));
    // Days
    Assert.assertEquals((Long) (1L), tu.fromEventDateAndEventTime("2021-03-21", "15:01:01")); // XX
    // Months
    Assert.assertEquals((Long) (DAY * 28L), tu.fromEventDateAndEventTime("2021-02", "15:01:01"));
    Assert.assertEquals((Long) (DAY * 29L), tu.fromEventDateAndEventTime("2024-02", "15:01:01"));
    // Years
    Assert.assertEquals((Long) (DAY * 365L), tu.fromEventDateAndEventTime("2021", "15:01:01"));
    Assert.assertEquals((Long) (DAY * 366L), tu.fromEventDateAndEventTime("2024", "15:01:01"));

    Assert.assertEquals((Long) (DAY * 3), tu.fromEventDateAndEventTime("2021-03-21/2021-03-23", "15:01:01"));
    Assert.assertEquals((Long) (DAY * (31 + 30)), tu.fromEventDateAndEventTime("2021-03/2021-04", "15:01:01"));
    Assert.assertEquals((Long) (DAY * 365 * 2), tu.fromEventDateAndEventTime("2021/2022", "15:01:01"));
    Assert.assertEquals((Long) (DAY * (365 * 3 + 1)), tu.fromEventDateAndEventTime("2022/2024", "15:01:01"));

    // Bad time
    Assert.assertEquals((Long) (DAY), tu.fromEventDateAndEventTime("2021-03-21", "AOEU"));
  }

  @Test
  public void illegalArgumentTests() throws Exception {
    try {
      tu.fromEventDateAndEventTime("ABCD", null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void nullArgumentTest() throws Exception {
    Assert.assertNull(tu.fromEventDateAndEventTime(null, null));
    Assert.assertNull(tu.fromEventDateAndEventTime(null, "12:00:00"));
  }
}

