package com.example.phonebill_android;

import edu.pdx.cs.joy.AbstractPhoneBill;
import edu.pdx.cs.joy.PhoneBillDumper;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class TextDumper implements PhoneBillDumper<AbstractPhoneBill<PhoneCall>> {
  private static final String DELIMITER = ",";
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(AbstractPhoneBill<PhoneCall> bill) throws IOException {
    if (bill == null) {
      return;
    }

    this.writer.write(bill.getCustomer() + "\n");
    Collection<PhoneCall> calls = bill.getPhoneCalls();
    for (PhoneCall call : calls) {
      this.writer.write(formatCall(call, bill.getCustomer()) + "\n");
    }
    this.writer.flush();
  }

  private String formatCall(PhoneCall call, String customer) {
    return customer + DELIMITER
        + call.getCaller() + DELIMITER
        + call.getCallee() + DELIMITER
        + formatDateTime(call.getBeginTime()) + DELIMITER
        + formatDateTime(call.getEndTime());
  }

  private String formatDateTime(LocalDateTime dateTime) {
    return dateTime.format(DATE_TIME_FORMATTER);
  }
}
