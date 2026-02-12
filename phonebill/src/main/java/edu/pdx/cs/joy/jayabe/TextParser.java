package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Parses a phone bill from a text file in CSV format.
 * The first line contains the customer name, followed by lines with phone call details.
 */
public class TextParser implements edu.pdx.cs.joy.PhoneBillParser<PhoneBill> {

  private final Reader reader;
  private static final String DELIMITER = ",";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = 
          DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  /**
   * Constructs a TextParser with the specified reader.
   *
   * @param reader the {@link Reader} to read the phone bill data from
   */
  public TextParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public PhoneBill parse() throws ParserException {
    try (BufferedReader br = new BufferedReader(reader)) {
      String firstLine = br.readLine();
      if (firstLine == null || firstLine.trim().isEmpty()) {
        throw new ParserException("File is empty or missing the customer name");
      }

      String customer = firstLine.trim();
      PhoneBill bill = new PhoneBill(customer);

      String line;
      while ((line = br.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }
        PhoneCall call = parseCall(line, customer);
        bill.addPhoneCall(call);
      }

      return bill;
    } catch (IOException e) {
      throw new ParserException("Error reading file: " + e.getMessage(), e);
    }
  }

  private PhoneCall parseCall(String line, String customer) throws ParserException {
    String[] parts = line.split(DELIMITER);
    if (parts.length != 5) {
      throw new ParserException("Malformatted line in text file (expected 5 fields): " + line);
    }

    // skip parts[0] since it should match the customer name and is redundant with the bill's customer
    try {
      String caller = parts[1].trim();
      String callee = parts[2].trim();
      String beginTime = parts[3].trim();
      String endTime = parts[4].trim();

      LocalDateTime begin = LocalDateTime.parse(beginTime, DATE_TIME_FORMATTER);
      LocalDateTime end = LocalDateTime.parse(endTime, DATE_TIME_FORMATTER);

      return new PhoneCall(customer, caller, callee, begin, end);
    } catch (java.time.format.DateTimeParseException e) {
      throw new ParserException("Invalid date/time format in line: " + line, e);
    } catch (Exception e) {
      throw new ParserException("Error parsing call: " + line + " - " + e.getMessage(), e);
    }
  }
}