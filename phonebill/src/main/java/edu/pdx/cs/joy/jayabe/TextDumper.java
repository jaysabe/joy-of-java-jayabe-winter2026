package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneBill;
import edu.pdx.cs.joy.PhoneBillDumper;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Dumps a phone bill to a text file in CSV format.
 * Each line contains a phone call with customer name, caller, callee, begin time, and end time.
 */
public class TextDumper implements PhoneBillDumper<AbstractPhoneBill<PhoneCall>> {
  private final Writer writer;
  private static final String DELIMITER = ",";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = 
          DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  /**
   * Constructs a TextDumper with the specified writer.
   *
   * @param writer the {@link Writer} to write the phone bill data to
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(AbstractPhoneBill<PhoneCall> bill) throws IOException {
    if (bill == null) {
      return;
    }
    
    // Write customer name on first line
    writer.write(bill.getCustomer() + "\n");
    
    // Write each phone call
    Collection<PhoneCall> calls = bill.getPhoneCalls();
    for (PhoneCall call : calls) {
      writer.write(formatCall(call, bill.getCustomer()) + "\n");
    }
    
    writer.flush();
  }

  private String formatCall(PhoneCall call, String customer) {
    return customer + DELIMITER + 
           call.getCaller() + DELIMITER + 
           call.getCallee() + DELIMITER + 
           formatDateTime(call.getBeginTime()) + DELIMITER + 
           formatDateTime(call.getEndTime());
  }

  private String formatDateTime(LocalDateTime dateTime) {
    return dateTime.format(DATE_TIME_FORMATTER);
  }
}