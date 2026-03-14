package com.example.phonebill_android;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PhoneBillServiceTest {

  private final PhoneBillService service = new PhoneBillService();

  @Test
  public void createPhoneCallWithValidInputsReturnsCall() {
    PhoneCall call = this.service.createPhoneCall(
        "Alice",
        "503-555-1000",
        "503-555-2000",
        "03/10/2026 9:15 PM",
        "03/10/2026 9:45 PM");

    assertEquals("Alice", call.getCustomer());
    assertEquals("503-555-1000", call.getCaller());
    assertEquals("503-555-2000", call.getCallee());
    assertEquals(LocalDateTime.of(2026, 3, 10, 21, 15), call.getBeginTime());
    assertEquals(LocalDateTime.of(2026, 3, 10, 21, 45), call.getEndTime());
  }

  @Test
  public void createPhoneCallRejectsMalformedPhoneNumber() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
        this.service.createPhoneCall(
            "Alice",
            "5035551000",
            "503-555-2000",
            "03/10/2026 9:15 PM",
            "03/10/2026 9:45 PM"));

    assertEquals(
        "Invalid caller number format: 5035551000 (expected nnn-nnn-nnnn)",
        ex.getMessage());
  }

  @Test
  public void parseCliDateTimeRejectsBadFormat() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
        this.service.parseCliDateTime("2026-03-10T21:15", "begin"));

    assertEquals(
        "Invalid begin date/time format: 2026-03-10T21:15 (expected MM/dd/yyyy h:mm a)",
        ex.getMessage());
  }

  @Test
  public void searchCallsBetweenReturnsInclusiveMatchesByBeginTime() {
    PhoneBill bill = new PhoneBill("Alice");
    bill.addPhoneCall(new PhoneCall(
        "Alice", "503-555-1000", "503-555-3000",
        LocalDateTime.of(2026, 3, 10, 8, 0),
        LocalDateTime.of(2026, 3, 10, 8, 10)));
    bill.addPhoneCall(new PhoneCall(
        "Alice", "503-555-1000", "503-555-4000",
        LocalDateTime.of(2026, 3, 10, 9, 0),
        LocalDateTime.of(2026, 3, 10, 9, 10)));
    bill.addPhoneCall(new PhoneCall(
        "Alice", "503-555-1000", "503-555-5000",
        LocalDateTime.of(2026, 3, 10, 10, 0),
        LocalDateTime.of(2026, 3, 10, 10, 10)));

    List<PhoneCall> results = this.service.searchCallsBetween(
        bill,
        LocalDateTime.of(2026, 3, 10, 9, 0),
        LocalDateTime.of(2026, 3, 10, 10, 0));

    assertEquals(2, results.size());
    assertEquals("503-555-4000", results.get(0).getCallee());
    assertEquals("503-555-5000", results.get(1).getCallee());
  }

  @Test
  public void searchCallsBetweenRejectsInvertedRange() {
    PhoneBill bill = new PhoneBill("Alice");

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
        this.service.searchCallsBetween(
            bill,
            LocalDateTime.of(2026, 3, 10, 12, 0),
            LocalDateTime.of(2026, 3, 10, 11, 0)));

    assertEquals("Search end time cannot be before begin time", ex.getMessage());
  }
}
