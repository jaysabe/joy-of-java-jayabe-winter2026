package com.example.phonebill_android;

import android.content.Context;

import edu.pdx.cs.joy.ParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class PhoneBillRepository {
  private static final String FILE_PREFIX = "phonebill-";
  private static final String FILE_SUFFIX = ".txt";

  public PhoneBill loadBill(Context context, String customer) throws IOException, ParserException {
    String fileName = toFileName(customer);
    File file = new File(context.getFilesDir(), fileName);
    if (!file.exists()) {
      return new PhoneBill(customer);
    }

    try (Reader reader = new InputStreamReader(
        context.openFileInput(fileName), StandardCharsets.UTF_8)) {
      PhoneBill bill = new TextParser(reader).parse();
      if (!bill.getCustomer().equals(customer)) {
        throw new IllegalStateException("Stored customer name does not match input customer");
      }
      return bill;
    }
  }

  public void saveBill(Context context, PhoneBill bill) throws IOException {
    String fileName = toFileName(bill.getCustomer());
    try (Writer writer = new OutputStreamWriter(
        context.openFileOutput(fileName, Context.MODE_PRIVATE), StandardCharsets.UTF_8)) {
      new TextDumper(writer).dump(bill);
    }
  }

  private String toFileName(String customer) {
    String normalized = customer.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    if (normalized.isEmpty()) {
      normalized = "customer";
    }

    return FILE_PREFIX + normalized + FILE_SUFFIX;
  }
}
