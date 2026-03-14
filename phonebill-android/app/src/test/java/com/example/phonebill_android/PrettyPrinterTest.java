package com.example.phonebill_android;

import org.junit.Test;

import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

public class PrettyPrinterTest {

  @Test
  public void prettyPrinterIncludesHeaderAndDuration() throws Exception {
    PhoneBill bill = new PhoneBill("Pretty Customer");
    bill.addPhoneCall(new PhoneCall(
        "Pretty Customer",
        "503-555-1111",
        "503-555-3333",
        LocalDateTime.of(2026, 3, 10, 10, 0),
        LocalDateTime.of(2026, 3, 10, 10, 30)));

    StringWriter writer = new StringWriter();
    new PrettyPrinter(writer).dump(bill);
    String output = writer.toString();

    assertTrue(output.contains("Phone Bill for: Pretty Customer"));
    assertTrue(output.contains("From: 503-555-1111"));
    assertTrue(output.contains("To:   503-555-3333"));
    assertTrue(output.contains("Duration: 30 minutes"));
  }

  @Test
  public void prettyPrinterShowsNoCallsMessageForEmptyBill() throws Exception {
    PhoneBill bill = new PhoneBill("Empty Customer");

    StringWriter writer = new StringWriter();
    new PrettyPrinter(writer).dump(bill);
    String output = writer.toString();

    assertTrue(output.contains("No phone calls on record."));
  }
}
