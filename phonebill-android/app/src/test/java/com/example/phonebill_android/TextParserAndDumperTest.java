package com.example.phonebill_android;

import edu.pdx.cs.joy.ParserException;

import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TextParserAndDumperTest {

  @Test
  public void dumpThenParseRoundTripPreservesBillData() throws Exception {
    PhoneBill original = new PhoneBill("Round Trip Customer");
    original.addPhoneCall(new PhoneCall(
        "Round Trip Customer",
        "503-555-1111",
        "503-555-2222",
        LocalDateTime.of(2026, 3, 10, 9, 0),
        LocalDateTime.of(2026, 3, 10, 9, 20)));

    StringWriter writer = new StringWriter();
    new TextDumper(writer).dump(original);

    PhoneBill parsed = new TextParser(new StringReader(writer.toString())).parse();

    assertEquals("Round Trip Customer", parsed.getCustomer());
    assertEquals(1, parsed.getPhoneCalls().size());
    PhoneCall call = parsed.getPhoneCalls().iterator().next();
    assertEquals("503-555-1111", call.getCaller());
    assertEquals("503-555-2222", call.getCallee());
    assertEquals(LocalDateTime.of(2026, 3, 10, 9, 0), call.getBeginTime());
    assertEquals(LocalDateTime.of(2026, 3, 10, 9, 20), call.getEndTime());
  }

  @Test
  public void parseRejectsEmptyInput() {
    ParserException ex = assertThrows(ParserException.class, () ->
        new TextParser(new StringReader("\n")).parse());

    assertEquals("File is empty or missing customer name", ex.getMessage());
  }

  @Test
  public void parseRejectsMalformedCallLine() {
    String content = "Alice\nAlice,503-555-1111,503-555-2222,03/10/2026 09:00\n";

    ParserException ex = assertThrows(ParserException.class, () ->
        new TextParser(new StringReader(content)).parse());

    assertEquals(
        "Expected 5 fields in line: Alice,503-555-1111,503-555-2222,03/10/2026 09:00",
        ex.getMessage());
  }
}
