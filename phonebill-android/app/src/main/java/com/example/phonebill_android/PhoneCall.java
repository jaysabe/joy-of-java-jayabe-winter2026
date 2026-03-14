package com.example.phonebill_android;

import edu.pdx.cs.joy.AbstractPhoneCall;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class PhoneCall extends AbstractPhoneCall implements Comparable<PhoneCall> {
  private static final DateTimeFormatter SHORT_FORMATTER =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

  private final String customer;
  private final String callerNumber;
  private final String calleeNumber;
  private final LocalDateTime begin;
  private final LocalDateTime end;

  public PhoneCall(String customer, String callerNumber, String calleeNumber,
                   LocalDateTime begin, LocalDateTime end) {
    this.customer = customer;
    this.callerNumber = callerNumber;
    this.calleeNumber = calleeNumber;
    this.begin = begin;
    this.end = end;
  }

  public String getCustomer() {
    return this.customer;
  }

  @Override
  public String getCaller() {
    return this.callerNumber;
  }

  @Override
  public String getCallee() {
    return this.calleeNumber;
  }

  @Override
  public LocalDateTime getBeginTime() {
    return this.begin;
  }

  @Override
  public String getBeginTimeString() {
    return this.begin.format(SHORT_FORMATTER);
  }

  @Override
  public LocalDateTime getEndTime() {
    return this.end;
  }

  @Override
  public String getEndTimeString() {
    return this.end.format(SHORT_FORMATTER);
  }

  @Override
  public int compareTo(PhoneCall other) {
    int beginComparison = this.begin.compareTo(other.begin);
    if (beginComparison != 0) {
      return beginComparison;
    }

    return this.callerNumber.compareTo(other.callerNumber);
  }
}
