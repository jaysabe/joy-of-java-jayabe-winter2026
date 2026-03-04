package edu.pdx.cs.joy.jayabe;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

/**
 * Serializes phone calls into a pipe-delimited text format.
 */
public class TextDumper {
  private final Writer writer;

  /**
   * Creates a dumper that writes to the given destination.
   *
   * @param writer Destination writer
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Writes each phone call as one line in the format
   * {@code caller|callee|begin|end}.
   *
   * @param calls Calls to serialize
   */
  public void dump(List<PhoneCallRecord> calls) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ){
      for (PhoneCallRecord call : calls) {
        pw.println(
          call.getCallerNumber() + "|" +
            call.getCalleeNumber() + "|" +
            call.getBeginAsString() + "|" +
            call.getEndAsString());
      }

      pw.flush();
    }
  }
}
