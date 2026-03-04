package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses pipe-delimited text into {@link PhoneCallRecord} objects.
 */
public class TextParser {
  private final Reader reader;

  /**
   * Creates a parser that reads from the provided source.
   *
   * @param reader Source reader
   */
  public TextParser(Reader reader) {
    this.reader = reader;
  }

  /**
   * Parses all phone call lines from the source text.
   *
   * @return Parsed list of phone calls
   * @throws ParserException If a line is malformed or an I/O error occurs
   */
  public List<PhoneCallRecord> parsePhoneCalls() throws ParserException {
    List<PhoneCallRecord> calls = new ArrayList<>();

    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (line.isBlank()) {
          continue;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
          throw new ParserException("Unexpected text: " + line);
        }

        calls.add(PhoneCallRecord.fromStrings(parts[0], parts[1], parts[2], parts[3]));
      }

    } catch (IOException e) {
      throw new ParserException("While parsing phone calls", e);
    }

    return calls;
  }
}
