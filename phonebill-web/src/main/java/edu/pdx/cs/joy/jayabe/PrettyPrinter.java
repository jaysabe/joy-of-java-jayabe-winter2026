package edu.pdx.cs.joy.jayabe;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class PrettyPrinter {
  private final Writer writer;


  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  public void dump(Map<String, String> dictionary) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ) {

      pw.println(String.format("Dictionary contains %d entries", dictionary.size()));

      for (Map.Entry<String, String> entry : dictionary.entrySet()) {
        String word = entry.getKey();
        String definition = entry.getValue();
        pw.println(formatDictionaryEntry(word, definition));
      }

      pw.flush();
    }

  }

  public void dump(String customer, List<PhoneCallRecord> calls) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ) {
      pw.println("Customer: " + customer);
      if (calls.isEmpty()) {
        pw.println("No phone calls found in the specified range");
      }

      for (PhoneCallRecord call : calls) {
        pw.println(call.getCallerNumber() + " -> " + call.getCalleeNumber() +
          " from " + call.getBeginAsString() + " to " + call.getEndAsString());
      }

      pw.flush();
    }
  }
}
