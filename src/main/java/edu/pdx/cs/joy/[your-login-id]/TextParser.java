package edu.pdx.cs.joy.[your-login-id];

import edu.pdx.cs.joy.PhoneBill;
import edu.pdx.cs.joy.PhoneBillParser;
import edu.pdx.cs.joy.PhoneCall;
import java.io.*;

public class TextParser implements PhoneBillParser {

  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public PhoneBill parse() throws ParserException {
    try (BufferedReader br = new BufferedReader(reader)) {
      String firstLine = br.readLine();
      if (firstLine == null || firstLine.trim().isEmpty()) {
        throw new ParserException("File is empty or missing customer name");
      }

      String customer = firstLine.trim();
      AbstractPhoneBill bill = new PhoneBillImpl(customer);

      String line;
      while ((line = br.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }
        PhoneCall call = parseCall(line);
        bill.addPhoneCall(call);
      }

      return bill;
    } catch (IOException e) {
      throw new ParserException("Error reading file: " + e.getMessage(), e);
    }
  }

  private PhoneCall parseCall(String line) throws ParserException {
    String[] parts = line.split("##");
    if (parts.length != 4) {
      throw new ParserException("Invalid call format: " + line);
    }

    try {
      String caller = parts[0].trim();
      String callee = parts[1].trim();
      String beginTime = parts[2].trim();
      String endTime = parts[3].trim();

      return new PhoneCall(caller, callee, beginTime, endTime);
    } catch (Exception e) {
      throw new ParserException("Error parsing call: " + line + " - " + e.getMessage(), e);
    }
  }
}
