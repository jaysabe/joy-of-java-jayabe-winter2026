package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneCall;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The <code>PhoneCall</code> class represents a single phone call between two parties.
 * It extends {@link AbstractPhoneCall} and encapsulates information about the caller,
 * callee, customer, and the start and end times of the call.
 *
 * <p>Each phone call is immutable once created, with all fields marked as final.
 * The class stores date and time information as {@link LocalDateTime} objects and
 * provides formatted string representations using a standardized date-time format.
 *
 * <p>Example usage:
 * <pre>
 *   LocalDateTime start = LocalDateTime.of(2025, 1, 15, 10, 30);
 *   LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
 *   PhoneCall call = new PhoneCall("John Doe", "503-555-1234",
 *                                   "503-555-5678", start, end);
 *   System.out.println(call.getCaller()); // prints "503-555-1234"
 * </pre>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class PhoneCall extends AbstractPhoneCall {
  /**
   * The name of the customer involved in this phone call.
   */
  private final String customer;
  /**
   * The phone number of the person who initiated the call.
   */
  private final String callerNumber;
  /**
   * The phone number of the person who received the call.
   */
  private final String calleeNumber;
  /**
   * The date and time when the phone call began.
   */
  private final LocalDateTime begin;
  /**
   * The date and time when the phone call ended.
   */
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
   * Returns the customer name for this phone call.
   * 
   * @return the customer's name
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