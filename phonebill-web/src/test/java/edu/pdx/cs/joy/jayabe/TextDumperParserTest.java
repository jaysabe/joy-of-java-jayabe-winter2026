package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextDumperParserTest {

  @Test
  void emptyPhoneCallListCanBeDumpedAndParsed() throws ParserException {
    List<PhoneCallRecord> calls = Collections.emptyList();
    List<PhoneCallRecord> read = dumpAndParse(calls);
    assertThat(read, equalTo(calls));
  }

  private List<PhoneCallRecord> dumpAndParse(List<PhoneCallRecord> calls) throws ParserException {
    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(calls);

    String text = sw.toString();

    TextParser parser = new TextParser(new StringReader(text));
    return parser.parsePhoneCalls();
  }

  @Test
  void dumpedPhoneCallTextCanBeParsed() throws ParserException {
    List<PhoneCallRecord> calls = List.of(
      PhoneCallRecord.fromStrings("503-111-1111", "503-222-2222", "03/01/2026 9:00 AM", "03/01/2026 9:30 AM"),
      PhoneCallRecord.fromStrings("503-333-3333", "503-444-4444", "03/02/2026 1:15 PM", "03/02/2026 1:45 PM")
    );

    List<PhoneCallRecord> read = dumpAndParse(calls);

    assertThat(read.size(), equalTo(2));
    assertThat(read.get(0).getCallerNumber(), equalTo("503-111-1111"));
    assertThat(read.get(0).getBeginAsString(), equalTo("03/01/2026 9:00 AM"));
    assertThat(read.get(1).getCalleeNumber(), equalTo("503-444-4444"));
  }

  @Test
  void malformedPhoneCallTextThrowsParserException() {
    TextParser parser = new TextParser(new StringReader("not|enough|parts"));
    assertThrows(ParserException.class, parser::parsePhoneCalls);
  }

  @Test
  void blankLinesAreIgnoredWhenParsing() throws ParserException {
    String text = "\n\n" +
      "503-111-1111|503-222-2222|03/01/2026 9:00 AM|03/01/2026 9:30 AM\n" +
      "\n";

    TextParser parser = new TextParser(new StringReader(text));
    List<PhoneCallRecord> calls = parser.parsePhoneCalls();

    assertThat(calls.size(), equalTo(1));
    assertThat(calls.get(0).getCallerNumber(), equalTo("503-111-1111"));
  }

  @Test
  void ioExceptionWhileParsingIsWrappedInParserException() {
    TextParser parser = new TextParser(new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("boom");
      }

      @Override
      public void close() {
      }
    });

    ParserException ex = assertThrows(ParserException.class, parser::parsePhoneCalls);
    assertThat(ex.getMessage(), equalTo("While parsing phone calls"));
  }
}
