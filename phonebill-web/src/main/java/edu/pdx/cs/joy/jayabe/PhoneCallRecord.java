package edu.pdx.cs.joy.jayabe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable data model for one phone call entry.
 */
public class PhoneCallRecord {
  /** Date/time format used by command-line and REST text payloads. */
  static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

  private final String callerNumber;
  private final String calleeNumber;
  private final LocalDateTime beginTime;
  private final LocalDateTime endTime;

  /**
   * Creates a phone call record.
   *
   * @param callerNumber Caller phone number
   * @param calleeNumber Callee phone number
   * @param beginTime Call begin time
   * @param endTime Call end time
   */
  public PhoneCallRecord(String callerNumber, String calleeNumber, LocalDateTime beginTime, LocalDateTime endTime) {
    this.callerNumber = callerNumber;
    this.calleeNumber = calleeNumber;
    this.beginTime = beginTime;
    this.endTime = endTime;
  }

  /**
   * Creates a phone call from textual date/time values.
   *
   * @param callerNumber Caller phone number
   * @param calleeNumber Callee phone number
   * @param begin Begin date/time text
   * @param end End date/time text
   * @return Parsed phone call record
   */
  public static PhoneCallRecord fromStrings(String callerNumber, String calleeNumber, String begin, String end) {
    LocalDateTime beginTime = LocalDateTime.parse(begin, DATE_TIME_FORMAT);
    LocalDateTime endTime = LocalDateTime.parse(end, DATE_TIME_FORMAT);
    return new PhoneCallRecord(callerNumber, calleeNumber, beginTime, endTime);
  }

  /**
   * @return Caller phone number
   */
  public String getCallerNumber() {
    return callerNumber;
  }

  /**
   * @return Callee phone number
   */
  public String getCalleeNumber() {
    return calleeNumber;
  }

  /**
   * @return Call begin time
   */
  public LocalDateTime getBeginTime() {
    return beginTime;
  }

  /**
   * @return Begin time formatted with {@link #DATE_TIME_FORMAT}
   */
  public String getBeginAsString() {
    return this.beginTime.format(DATE_TIME_FORMAT);
  }

  /**
   * @return End time formatted with {@link #DATE_TIME_FORMAT}
   */
  public String getEndAsString() {
    return this.endTime.format(DATE_TIME_FORMAT);
  }

  /**
   * Indicates whether the call start is within an inclusive range.
   *
   * @param begin Inclusive begin bound
   * @param end Inclusive end bound
   * @return {@code true} if call begin time is in range
   */
  public boolean beginsBetween(LocalDateTime begin, LocalDateTime end) {
    return !this.beginTime.isBefore(begin) && !this.beginTime.isAfter(end);
  }
}