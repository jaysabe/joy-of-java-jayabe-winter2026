package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneBill;
import edu.pdx.cs.joy.PhoneBillDumper;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * The <code>PrettyPrinter</code> class implements a pretty printer for phone bills.
 * It creates a nicely-formatted textual presentation of a phone bill and its calls,
 * sorted chronologically by begin time.
 *
 * <p>The pretty printer displays:
 * <ul>
 *   <li>Customer name as a header</li>
 *   <li>Each phone call with caller, callee, begin time, end time, and duration in minutes</li>
 *   <li>Formatted dates and times using localized short format</li>
 * </ul>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class PrettyPrinter implements PhoneBillDumper<AbstractPhoneBill<PhoneCall>> {

  private final Writer writer;
  private static final DateTimeFormatter PRETTY_FORMATTER = 
          DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

  /**
   * Creates a new PrettyPrinter that will write to the specified writer.
   *
   * @param writer the writer to output the pretty-printed phone bill
   */
  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  /**
   * Pretty prints the phone bill to the writer.
   * Displays the customer name followed by a formatted list of all phone calls,
   * sorted chronologically with call duration in minutes.
   *
   * @param bill the phone bill to pretty print
   * @throws IOException if there is an error writing to the output
   */
  @Override
  public void dump(AbstractPhoneBill<PhoneCall> bill) throws IOException {
    if (bill == null) {
      return;
    }
    
    PrintWriter pw = new PrintWriter(writer);
    
    // Print header
    pw.println("========================================");
    pw.println("Phone Bill for: " + bill.getCustomer());
    pw.println("========================================");
    pw.println();
    
    // Print each phone call
    Collection<PhoneCall> calls = bill.getPhoneCalls();
    if (calls.isEmpty()) {
      pw.println("No phone calls on record.");
    } else {
      for (PhoneCall call : calls) {
        printCall(pw, call);
        pw.println();
      }
    }
    
    pw.flush();
  }

  /**
   * Prints a single phone call in a pretty format.
   *
   * @param pw the print writer to write to
   * @param call the phone call to print
   */
  private void printCall(PrintWriter pw, PhoneCall call) {
    String caller = call.getCaller();
    String callee = call.getCallee();
    LocalDateTime begin = call.getBeginTime();
    LocalDateTime end = call.getEndTime();
    long duration = ChronoUnit.MINUTES.between(begin, end);
    
    pw.println("From: " + caller);
    pw.println("To:   " + callee);
    pw.println("Begin: " + formatDateTime(begin));
    pw.println("End:   " + formatDateTime(end));
    pw.println("Duration: " + duration + " minute" + (duration != 1 ? "s" : ""));
  }

  /**
   * Formats a LocalDateTime using a pretty, localized short format.
   *
   * @param dateTime the date and time to format
   * @return a formatted string representation of the date and time
   */
  private String formatDateTime(LocalDateTime dateTime) {
    return dateTime.format(PRETTY_FORMATTER);
  }
}
