package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.PhoneBillDumper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * The <code>TextDumper</code> class implements the {@link PhoneBillDumper} interface.
 * It dumps the contents of a {@link PhoneBill} to a text file, including the
 * customer name and all associated phone calls.
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class TextDumper implements PhoneBillDumper<PhoneBill> {
  private final Writer writer;

  /**
   * Constructs a new <code>TextDumper</code> with the specified writer.
   * @param writer the {@link Writer} to write phone bill data to
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Dumps the contents of a phone bill to the text-based source.
   * The customer name is written on the first line, followed by each phone call.
   *
   * @param bill the {@link PhoneBill} to dump
   * @throws IOException if an I/O error occurs while writing
   */
  @Override
  public void dump(PhoneBill bill) throws IOException {
    if (bill == null) {
      return;
    }
    PrintWriter pw = new PrintWriter(this.writer);

    // Requirement: Write the customer name to the file
    pw.println(bill.getCustomer());
    // Iterate through all phone calls and write them in a comma-separated format
    Collection<PhoneCall> calls = bill.getPhoneCalls();
    for (PhoneCall call : calls) {
      pw.print(call.getCustomer());
      pw.print(",");
      pw.print(call.getCaller());
      pw.print(",");
      pw.print(call.getCallee());
      pw.print(",");
      pw.print(call.getBeginTimeString());
      pw.print(",");
      pw.println(call.getEndTimeString());
    }

    pw.flush();
  }
}