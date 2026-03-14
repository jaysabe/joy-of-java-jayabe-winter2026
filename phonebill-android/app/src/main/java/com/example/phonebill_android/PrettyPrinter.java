package com.example.phonebill_android;

import edu.pdx.cs.joy.AbstractPhoneBill;
import edu.pdx.cs.joy.PhoneBillDumper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

public class PrettyPrinter implements PhoneBillDumper<AbstractPhoneBill<PhoneCall>> {
  private static final DateTimeFormatter PRETTY_FORMATTER =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

  private final Writer writer;

  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(AbstractPhoneBill<PhoneCall> bill) throws IOException {
    if (bill == null) {
      return;
    }

    PrintWriter printWriter = new PrintWriter(this.writer);
    printWriter.println("========================================");
    printWriter.println("Phone Bill for: " + bill.getCustomer());
    printWriter.println("========================================");
    printWriter.println();

    Collection<PhoneCall> calls = bill.getPhoneCalls();
    if (calls.isEmpty()) {
      printWriter.println("No phone calls on record.");
    } else {
      for (PhoneCall call : calls) {
        printCall(printWriter, call);
        printWriter.println();
      }
    }

    printWriter.flush();
  }

  private void printCall(PrintWriter printWriter, PhoneCall call) {
    LocalDateTime begin = call.getBeginTime();
    LocalDateTime end = call.getEndTime();
    long duration = ChronoUnit.MINUTES.between(begin, end);

    printWriter.println("From: " + call.getCaller());
    printWriter.println("To:   " + call.getCallee());
    printWriter.println("Begin: " + formatDateTime(begin));
    printWriter.println("End:   " + formatDateTime(end));
    printWriter.println("Duration: " + duration + " minute" + (duration == 1 ? "" : "s"));
  }

  private String formatDateTime(LocalDateTime dateTime) {
    return dateTime.format(PRETTY_FORMATTER);
  }
}
