package com.example.phonebill_android;

import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextParser implements edu.pdx.cs.joy.PhoneBillParser<PhoneBill> {
  private static final String DELIMITER = ",";
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public PhoneBill parse() throws ParserException {
    try (BufferedReader bufferedReader = new BufferedReader(this.reader)) {
      String firstLine = bufferedReader.readLine();
      if (firstLine == null || firstLine.trim().isEmpty()) {
        throw new ParserException("File is empty or missing customer name");
      }

      String customer = firstLine.trim();
      PhoneBill bill = new PhoneBill(customer);

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }

        bill.addPhoneCall(parseCall(line, customer));
      }

      return bill;
    } catch (IOException ex) {
      throw new ParserException("Error reading file: " + ex.getMessage(), ex);
    }
  }

  private PhoneCall parseCall(String line, String customer) throws ParserException {
    String[] parts = line.split(DELIMITER);
    if (parts.length != 5) {
      throw new ParserException("Expected 5 fields in line: " + line);
    }

    try {
      String caller = parts[1].trim();
      String callee = parts[2].trim();
      LocalDateTime begin = LocalDateTime.parse(parts[3].trim(), DATE_TIME_FORMATTER);
      LocalDateTime end = LocalDateTime.parse(parts[4].trim(), DATE_TIME_FORMATTER);
      return new PhoneCall(customer, caller, callee, begin, end);
    } catch (Exception ex) {
      throw new ParserException("Unable to parse call line: " + line, ex);
    }
  }
}
