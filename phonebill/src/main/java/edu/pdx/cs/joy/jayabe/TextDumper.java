package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.PhoneBillDumper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * Dumps a PhoneBill to a text-based destination using a specified format. TODO : edit this
 */
public class TextDumper implements PhoneBillDumper<PhoneBill> {
  private final Writer writer;

  /**
   * Constructs a TextDumper that writes to the given Writer.
   * @param writer the destination for the dumped phone bill data
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * TODO : Write this
   * */
  @Override
  public void dump(PhoneBill bill) {
    PrintWriter pw = new PrintWriter(this.writer);

    // First line: Customer name
    pw.println(bill.getCustomer());

    Collection<PhoneCall> calls = bill.getPhoneCalls();
    for (PhoneCall call : calls) {
      // Use the shared formatter from Project2 for consistency
      String begin = call.getBeginTime().format(Project2.DATE_TIME_FORMATTER);
      String end = call.getEndTime().format(Project2.DATE_TIME_FORMATTER);

      // Format: Caller, Callee, Start, End
      pw.println(String.format("%s,%s,%s,%s",
              call.getCaller(),
              call.getCallee(),
              begin,
              end));
    }
    pw.flush();
  }
}