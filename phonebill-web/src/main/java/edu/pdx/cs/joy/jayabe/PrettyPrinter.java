package edu.pdx.cs.joy.jayabe;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

/**
 * Writes a human-readable phone bill report.
 */
public class PrettyPrinter {
  private final Writer writer;


  /**
   * Creates a pretty printer that writes to the given destination.
   *
   * @param writer Destination writer
   */
  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  /**
   * Writes a formatted report for one customer and their matching calls.
   *
   * @param customer Customer name
   * @param calls Calls to print
   */
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
