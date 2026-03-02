package edu.pdx.cs.joy.jayabe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PhoneCallRecord {
  static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

  private final String callerNumber;
  private final String calleeNumber;
  private final LocalDateTime beginTime;
  private final LocalDateTime endTime;

  public PhoneCallRecord(String callerNumber, String calleeNumber, LocalDateTime beginTime, LocalDateTime endTime) {
    this.callerNumber = callerNumber;
    this.calleeNumber = calleeNumber;
    this.beginTime = beginTime;
    this.endTime = endTime;
  }

  public static PhoneCallRecord fromStrings(String callerNumber, String calleeNumber, String begin, String end) {
    LocalDateTime beginTime = LocalDateTime.parse(begin, DATE_TIME_FORMAT);
    LocalDateTime endTime = LocalDateTime.parse(end, DATE_TIME_FORMAT);
    return new PhoneCallRecord(callerNumber, calleeNumber, beginTime, endTime);
  }

  public String getCallerNumber() {
    return callerNumber;
  }

  public String getCalleeNumber() {
    return calleeNumber;
  }

  public LocalDateTime getBeginTime() {
    return beginTime;
  }

  public String getBeginAsString() {
    return this.beginTime.format(DATE_TIME_FORMAT);
  }

  public String getEndAsString() {
    return this.endTime.format(DATE_TIME_FORMAT);
  }

  public boolean beginsBetween(LocalDateTime begin, LocalDateTime end) {
    return !this.beginTime.isBefore(begin) && !this.beginTime.isAfter(end);
  }
}