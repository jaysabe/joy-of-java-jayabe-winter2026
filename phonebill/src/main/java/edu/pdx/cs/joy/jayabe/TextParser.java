package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.PhoneBillParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * Parses a PhoneBill from a text file
 */
public class TextParser implements PhoneBillParser<PhoneBill> {
  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public PhoneBill parse() throws ParserException {
    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {
      String customer = br.readLine();
      if (customer == null || customer.isBlank()) {
        throw new ParserException(("The tect file is empty or missing the customer name."))
      }

      PhoneBill bill = new PhoneBill(customer);

      String line = null;
      while ((line = br.readLine()) != null) {
        if (line.isBlank()) continue;

        // Split the line based on the format used in TextDumper
        String [] parts = line.split(",");
        if (parts.length != 4) {
          throw new ParserException(("Malformatted line in text file: " + line));
        }

        try {
          PhoneCall call = new PhoneCall(parts[0], parts[1], parts[2], parts[3]);
          bill.addPhoneCall(call);
        } catch (IllegalArgumentException e) {
          throw new ParserException("Invalid call data in file: " + e.getMessage());
        }
      }

      return bill;

    } catch (IOException e) {
      throw new ParserException("While parsing phone bill text", e);
    }
  }
}
