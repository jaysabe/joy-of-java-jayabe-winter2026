package edu.pdx.cs.joy.jayabe;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class TextDumper {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(Map<String, String> dictionary) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ){
      for (Map.Entry<String, String> entry : dictionary.entrySet()) {
        pw.println(entry.getKey() + " : " + entry.getValue());
      }

      pw.flush();
    }
  }

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
