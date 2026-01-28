package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneCall;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PhoneCall extends AbstractPhoneCall {
  private final String customer;
  private final String callerNumber;
  private final String calleeNumber;
  private final LocalDateTime begin;
  private final LocalDateTime end;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  /**
   * Creates a new phone call
   * @param customer The customer making the call (can contain any characters including numbers)
   * @param callerNumber The phone number of the person initiating the call
   * @param calleeNumber The phone number of the person receiving the call
   * @param begin The date and time when the call began (formatted as a string, e.g., "01/15/2025 10:30 AM")
   * @param end The date and time when the call ended (formatted as a string, e.g., "01/15/2025 10:45 AM")
   */
  public PhoneCall(String customer, String callerNumber, String calleeNumber, LocalDateTime begin, LocalDateTime end) {
    this.customer = customer;
    this.callerNumber = callerNumber;
    this.calleeNumber = calleeNumber;
    this.begin = begin;
    this.end = end;
  }

  /**
   * Returns the customer name
   * @return The customer's name
   */
  public String getCustomer() {
    return this.customer;
  }

  /**
   * Returns the phone number of the person who initiated this call
   * @return The caller's phone number
   */
  @Override
  public String getCaller() {
    return this.callerNumber;
  }

  /**
   * Returns the phone number of the person who received this call
   * @return The callee's phone number
   */
  @Override
  public String getCallee() {
    return this.calleeNumber;
  }

  @Override
  public LocalDateTime getBeginTime(){
    return this.begin;
  }
  /**
   * Returns the date and time when this call began
   * @return A string representation of when the call began
   */
  @Override
  public String getBeginTimeString() {
    return this.begin.format(FORMATTER);
  }

  @Override
  public LocalDateTime getEndTime(){
    return this.end;
  }
  /**
   * Returns the date and time when this call ended
   * @return A string representation of when the call ended
   */
  @Override
  public String getEndTimeString() {
    return this.end.format(FORMATTER);
  }
}