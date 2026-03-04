package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneCallRecordTest {

  @Test
  void getBeginTimeReturnsBeginTimeProvidedAtConstruction() {
    LocalDateTime begin = LocalDateTime.of(2026, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2026, 3, 1, 9, 30);

    PhoneCallRecord call = new PhoneCallRecord("503-111-1111", "503-222-2222", begin, end);

    assertThat(call.getBeginTime(), equalTo(begin));
  }

  @Test
  void beginsBetweenIncludesRangeEndpoints() {
    LocalDateTime begin = LocalDateTime.of(2026, 3, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2026, 3, 1, 9, 30);
    PhoneCallRecord call = new PhoneCallRecord("503-111-1111", "503-222-2222", begin, end);

    assertTrue(call.beginsBetween(begin, end));
  }

  @Test
  void beginsBetweenIsFalseWhenCallStartsBeforeRange() {
    LocalDateTime callBegin = LocalDateTime.of(2026, 3, 1, 8, 59);
    LocalDateTime callEnd = LocalDateTime.of(2026, 3, 1, 9, 30);
    PhoneCallRecord call = new PhoneCallRecord("503-111-1111", "503-222-2222", callBegin, callEnd);

    assertFalse(call.beginsBetween(
      LocalDateTime.of(2026, 3, 1, 9, 0),
      LocalDateTime.of(2026, 3, 1, 10, 0)
    ));
  }

  @Test
  void beginsBetweenIsFalseWhenCallStartsAfterRange() {
    LocalDateTime callBegin = LocalDateTime.of(2026, 3, 1, 10, 1);
    LocalDateTime callEnd = LocalDateTime.of(2026, 3, 1, 10, 30);
    PhoneCallRecord call = new PhoneCallRecord("503-111-1111", "503-222-2222", callBegin, callEnd);

    assertFalse(call.beginsBetween(
      LocalDateTime.of(2026, 3, 1, 9, 0),
      LocalDateTime.of(2026, 3, 1, 10, 0)
    ));
  }
}
