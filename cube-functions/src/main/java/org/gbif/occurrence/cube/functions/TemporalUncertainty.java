package org.gbif.occurrence.cube.functions;

import org.gbif.api.util.IsoDateInterval;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

/**
 * Temporal uncertainty.
 *
 * https://docs.gbif-uat.org/b-cubed/1.0/en/#minimum-temporal-uncertainty
 */
public class TemporalUncertainty {

  private static final DateTimeFormatter TIME_PATTERN =
    DateTimeFormatter.ofPattern("HH:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]][XXXXX][XXXX][XXX][XX][X]");

  private static final long DAY_SECONDS = 60 * 60 * 24;

  public TemporalUncertainty() {
  }

  /**
   * Calculate the temporal uncertainty.
   */
  public Long fromEventDateAndEventTime(String eventDateInterval, String eventTime) throws Exception {
    // SQL null-like handling behaviour
    if (eventDateInterval == null) {
      return null;
    }

    try {
      IsoDateInterval interval = IsoDateInterval.fromString(eventDateInterval);

      Temporal from = interval.getFrom();
      Temporal to = interval.getTo();

      if (to != null) {
        if (from instanceof LocalDateTime
          || from instanceof OffsetDateTime
          || from instanceof ZonedDateTime) {
          return ChronoUnit.SECONDS.between(from, to);
        }

        LocalDate fromDay, toDay;
        if (from instanceof Year) {
          fromDay = ((Year) from).atDay(1);
          toDay = ((Year) to).atMonthDay(MonthDay.of(12, 31));
        } else if (from instanceof YearMonth) {
          fromDay = ((YearMonth) from).atDay(1);
          toDay = ((YearMonth) to).atEndOfMonth();
        } else { // from instanceof LocalDate
          fromDay = (LocalDate) from;
          toDay = (LocalDate) to;
        }

        return ChronoUnit.SECONDS.between(fromDay.atStartOfDay(), toDay.plusDays(1).atStartOfDay());
      }

      if (from instanceof Year) {
        if (((Year) from).isLeap()) {
          return 366 * DAY_SECONDS;
        } else {
          return 365 * DAY_SECONDS;
        }
      } else if (from instanceof YearMonth) {
        return ((YearMonth) from).lengthOfMonth() * DAY_SECONDS;
      } else if (from instanceof LocalDate) {
        // Check for an eventTime
        if (eventTime != null) {
          try {
            TemporalAccessor time = TIME_PATTERN.parseBest(
              eventTime,
              OffsetTime::from,
              LocalTime::from);
            if (time instanceof OffsetTime) {
              return fromDateTime(((LocalDate) from).atTime((OffsetTime) time));
            } else {
              return fromDateTime(((LocalDate) from).atTime((LocalTime) time));
            }
          } catch (Exception e) {
            // Ignore problems with the eventTime.
          }
        }

        return DAY_SECONDS;
      } else if (from instanceof LocalDateTime
        || from instanceof OffsetDateTime
        || from instanceof ZonedDateTime) {
        return fromDateTime(from);
      }

      return null;
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private Long fromDateTime(Temporal dateTime) {
    if (dateTime.getLong(ChronoField.SECOND_OF_MINUTE) == 0) {
      if (dateTime.getLong(ChronoField.MINUTE_OF_HOUR) == 0) {
        if (dateTime.getLong(ChronoField.HOUR_OF_DAY) == 0) {
          return DAY_SECONDS;
        } else {
          return 60 * 60L;
        }
      } else {
        return 60L;
      }
    } else {
      return 1L;
    }
  }
}
