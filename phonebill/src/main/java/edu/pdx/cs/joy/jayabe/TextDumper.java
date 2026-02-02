package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.PhoneBillDumper;
import org.jspecify.annotations.NonNull;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

/**
 *  Dumps a PhoneBill to a text file
 */
public class TextDumper implements PhoneBillDumper<PhoneBill> {
  private final String fileName;

  public TextDumper(String fileName){ this.fileName = fileName; }

  public void dump(PhoneBill bill) throws IOException {
    try (
      PrintWriter pw = new PrintWriter(new File(this.fileName))
    ) {
      pw.println(bill.getCustomer());

      Collection<PhoneCall> calls = bill.getPhoneCalls();
      for (PhoneCall call : calls) {
        // Format: Caller, Callee, Start, End
        pw.println(call.getCaller() + "," +
                call.getCallee()  + "," +
                call.getBeginTimeString()  + "," +
                call.getEndTimeString());
      }
      pw.flush();
    }
  }
}
