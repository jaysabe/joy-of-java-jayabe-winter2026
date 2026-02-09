package edu.pdx.cs.joy.[your-login-id];

import edu.pdx.cs.joy.PhoneBill;
import edu.pdx.cs.joy.PhoneBillDumper;
import edu.pdx.cs.joy.PhoneCall;
import java.io.*;
import java.util.Collection;

public class TextDumper implements PhoneBillDumper {

  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(PhoneBill bill) throws IOException {
    writer.write(bill.getCustomer() + "\n");
    
    Collection<PhoneCall> calls = bill.getPhoneCalls();
    for (PhoneCall call : calls) {
      writer.write(formatCall(call) + "\n");
    }
    
    writer.flush();
  }

  private String formatCall(PhoneCall call) {
    return call.getCaller() + "##" + 
           call.getCallee() + "##" + 
           call.getBeginTimeString() + "##" + 
           call.getEndTimeString();
  }
}
