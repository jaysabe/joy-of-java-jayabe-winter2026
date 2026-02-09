package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextDumperTest {
  @Test
  void dumperHandlesSpecialCharactersInCustomerName() throws IOException {
    String weirdName = "John O'Doe-Smith, Jr.";
    PhoneBill bill = new PhoneBill(weirdName);

    StringWriter sw = new StringWriter();
    new TextDumper(sw).dump(bill);

    assertThat(sw.toString(), startsWith(weirdName));
  }

  @Test
  void dumperWritesCustomerNameOnFirstLine() throws IOException {
    String customer = "Test Phone Bill";
    PhoneBill bill = new PhoneBill(customer);

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(bill);

    String text = sw.toString();
    assertThat(text, startsWith(customer));
  }

  @Test
  void phoneBillOwnerIsDumpedInTextFormat() throws IOException {
    String customer = "Test Phone Bill";
    PhoneBill bill = new PhoneBill(customer);

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(bill);

    String text = sw.toString();
    assertThat(text, containsString(customer));
  }

  @Test
  void canParseTextWrittenByTextDumper(@TempDir File tempDir) throws IOException, ParserException {
    String customer = "Test Phone Bill";
    PhoneBill bill = new PhoneBill(customer);

    LocalDateTime now = LocalDateTime.now();
    bill.addPhoneCall(new PhoneCall(customer, "123-456-7890", "098-765-4321", now, now.plusMinutes(10)));

    File textFile = new File(tempDir, "phonebill-1.txt");

    try (FileWriter fw = new FileWriter(textFile)) {
      TextDumper dumper = new TextDumper(fw);
      dumper.dump(bill); // Use the bill object that actually has the call
    }

    try (FileReader fr = new FileReader(textFile)) {
      TextParser parser = new TextParser(fr);
      PhoneBill read = parser.parse();
      assertThat(read.getCustomer(), containsString(customer));
    }
  }

  @Test
  void dumperExportsPhoneCallDetailsCorrectly() throws IOException {
    String customer = "Customer Name";
    PhoneBill bill = new PhoneBill(customer);

    String caller = "123-456-7890";
    String callee = "987-654-3210";
    // Using a specific date to check formatting
    java.time.LocalDateTime start = java.time.LocalDateTime.of(2023, 1, 15, 13, 45);
    java.time.LocalDateTime end = java.time.LocalDateTime.of(2023, 1, 15, 14, 0);

    bill.addPhoneCall(new PhoneCall(customer, caller, callee, start, end));

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(bill);

    String output = sw.toString();
    String expectedDateString = start.format(Project2.DATE_TIME_FORMATTER);
    assertThat(output, containsString(customer + "," + caller + "," + callee + "," + expectedDateString));
  }

  @Test
  void dumperHandlesMultiplePhoneCalls() throws IOException {
    String customer = "Multi-call Bill";
    PhoneBill bill = new PhoneBill(customer);

    bill.addPhoneCall(new PhoneCall(customer, "111-111-1111", "222-222-2222",
            LocalDateTime.of(2025, 1, 15, 10, 0), LocalDateTime.of(2025, 1, 15, 10, 23)));
    bill.addPhoneCall(new PhoneCall(customer, "333-333-3333", "444-444-4444",
            LocalDateTime.of(2025, 1, 16, 12, 0), LocalDateTime.of(2025, 1, 16, 12, 14)));

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(bill);

    String output = sw.toString();
    String[] lines = output.split("\n");

    assertThat(lines.length, equalTo(3));
    assertThat(lines[0].trim(),
            new StringContains(customer));

    assertThat(lines[1], containsString("111-111-1111"));
    assertThat(lines[2], containsString("333-333-3333"));
  }

  @Test
  void dumpingNullBillDoesNotThrowException() throws IOException {
    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    // This tests the null check we added to the dump method
    dumper.dump(null);
    assertThat(sw.toString(), equalTo(""));
  }

  @Test
  void dumperImplementsCorrectInterface() {
    TextDumper dumper = new TextDumper(new StringWriter());
    // Technical check: Ensure TextDumper is an instance of the Dumper interface for PhoneBill
    assertTrue(dumper instanceof edu.pdx.cs.joy.PhoneBillDumper);
  }

  @Test
  void invokeMain() {
    // This test is to check if the main method is working correctly
    // You can add more test cases as needed
  }
}
