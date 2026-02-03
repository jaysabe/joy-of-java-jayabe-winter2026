package edu.pdx.cs.joy.jayabe;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.PhoneBillParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * The <code>TextParser</code> class parses a {@link PhoneBill} from a text file.
 * It implements the {@link PhoneBillParser} interface to read phone bill data from
 * a text-based source and reconstruct a PhoneBill object with its associated phone calls.
 *
 * <p>The expected text file format is:
 * <ul>
 *   <li>First line: Customer name</li>
 *   <li>Subsequent lines: Comma-separated phone call data with 4 fields:
 *       caller number, callee number, begin time, and end time</li>
 * </ul>
 *
 * <p>Example file format:
 * <pre>
 * John Doe
 * 503-555-1234,503-555-5678,01/15/2025 10:30,01/15/2025 10:45
 * 503-555-1234,503-555-9999,01/16/2025 14:00,01/16/2025 14:20
 * </pre>
 *
 * <p>The parser validates the file structure and phone call data, throwing
 * {@link ParserException} if the file is malformed or contains invalid data.
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class TextParser implements PhoneBillParser<PhoneBill> {
  private final Reader reader;

  /**
   * Constructs a new <code>TextParser</code> with the specified reader.
   * The reader should be positioned at the beginning of the phone bill text data.
   *
   * @param reader the {@link Reader} to read phone bill data from; must not be null
   */
  public TextParser(Reader reader) {
    this.reader = reader;
  }

  /**
   * Returns the reader used by this parser for reading phone bill data.
   * This method is visible for testing purposes only.
   *
   * @return the {@link Reader} associated with this parser
   */
  @VisibleForTesting
  public Reader getTextReader(){return this.reader; }

  /**
   * Parses and reconstructs a {@link PhoneBill} from the text source.
   *
   * <p>The parsing process:
   * <ol>
   *   <li>Reads the first line to get the customer name</li>
   *   <li>Creates a new PhoneBill for that customer</li>
   *   <li>Reads subsequent lines, parsing each as a phone call</li>
   *   <li>Adds each valid phone call to the bill</li>
   * </ol>
   *
   * <p>Each phone call line must contain exactly 4 comma-separated fields:
   * caller number, callee number, begin time, and end time.
   *
   * <p>Blank lines in the file are ignored.
   *
   * @return a {@link PhoneBill} object containing the customer name and all parsed phone calls
   * @throws ParserException if the file is empty, missing the customer name, contains
   *                         malformatted lines, has invalid phone call data, or an I/O
   *                         error occurs while reading the file
   */
  @Override
  public PhoneBill parse() throws ParserException {
    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {
      String customer = br.readLine();
      if (customer == null || customer.isBlank()) {
        throw new ParserException(("The text file is empty or missing the customer name."));
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
          PhoneCall call = new PhoneCall(parts[0], parts[1], parts[2], parts[3], parts[4]);
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
