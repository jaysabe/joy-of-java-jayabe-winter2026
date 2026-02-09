package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextParserTest {

  @Test
  void canParseValidPhoneBillWithMultipleCalls() throws ParserException {
    String customer = "John Doe";
    String call1 = customer + ",503-555-1234,503-555-6789,01/15/2025 10:00,01/15/2025 10:30";
    String call2 = customer + ",503-555-1234,503-555-9999,01/16/2025 12:00,01/16/2025 12:15";
    String content = customer + "\n" + call1 + "\n" + call2;

    TextParser parser = new TextParser(new StringReader(content));
    PhoneBill bill = parser.parse();

    assertThat(bill.getCustomer(), equalTo(customer));
    Collection<PhoneCall> calls = bill.getPhoneCalls();
    assertThat(calls, hasSize(2));
  }

  @Test
  void emptyFileThrowsParserException() {
    String content = "";
    TextParser parser = new TextParser(new StringReader(content));

    ParserException ex = assertThrows(ParserException.class, parser::parse);
    assertThat(ex.getMessage(), containsString("empty or missing the customer name"));
  }

  @Test
  void malformattedLineMissingFieldsThrowsParserException() {
    String content = "John Doe\nJohn Doe,503-555-1234,503-555-6789,01/15/2025 10:00"; // Missing end time
    TextParser parser = new TextParser(new StringReader(content));

    ParserException ex = assertThrows(ParserException.class, parser::parse);
    assertThat(ex.getMessage(), containsString("expected 5 fields"));
  }

  @Test
  void invalidDateFormatThrowsParserException() {
    // Note the "2025/15/01" which doesn't match MM/dd/yyyy
    String content = "John Doe\nJohn Doe,503-555-1234,503-555-6789,2025/15/01 10:00,01/15/2025 10:30";
    TextParser parser = new TextParser(new StringReader(content));

    ParserException ex = assertThrows(ParserException.class, parser::parse);
    assertThat(ex.getMessage(), containsString("Invalid date/time format"));
  }

  @Test
  void blankLinesBetweenCallsAreIgnored() throws ParserException {
    String content = "John Doe\n\nJohn Doe,503-555-1234,503-555-6789,01/15/2025 10:00,01/15/2025 10:30\n\n";
    TextParser parser = new TextParser(new StringReader(content));

    PhoneBill bill = parser.parse();
    assertThat(bill.getPhoneCalls(), hasSize(1));
  }
}