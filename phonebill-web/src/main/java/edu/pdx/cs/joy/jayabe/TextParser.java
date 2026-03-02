package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  public Map<String, String> parse() throws ParserException {
    Pattern pattern = Pattern.compile("(.*) : (.*)");

    Map<String, String> map = new HashMap<>();

    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {

      for (String line = br.readLine(); line != null; line = br.readLine()) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
          throw new ParserException("Unexpected text: " + line);
        }

        String word = matcher.group(1);
        String definition = matcher.group(2);

        map.put(word, definition);
      }

    } catch (IOException e) {
      throw new ParserException("While parsing dictionary", e);
    }

    return map;
  }

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
