package com.example.phonebill_android.it;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.phonebill_android.PhoneBill;
import com.example.phonebill_android.PhoneBillRepository;
import com.example.phonebill_android.PhoneCall;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PhoneBillRepositoryStorageIntegrationTest {

  private static final String CUSTOMER = "Acme, Inc. Portland";

  private final PhoneBillRepository repository = new PhoneBillRepository();

  @After
  public void cleanup() {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    context.deleteFile(fileNameFor(CUSTOMER));
  }

  @Test
  public void saveAndLoadBillPreservesCustomerNameWhenFileNameIsNormalized() throws Exception {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    PhoneBill bill = new PhoneBill(CUSTOMER);
    bill.addPhoneCall(new PhoneCall(
        CUSTOMER,
        "503-555-1000",
        "503-555-2000",
        LocalDateTime.of(2026, 3, 12, 8, 15),
        LocalDateTime.of(2026, 3, 12, 8, 45)));

    this.repository.saveBill(context, bill);

    PhoneBill loaded = this.repository.loadBill(context, CUSTOMER);

    assertEquals(CUSTOMER, loaded.getCustomer());
    assertEquals(1, loaded.getPhoneCalls().size());
    PhoneCall loadedCall = loaded.getPhoneCalls().iterator().next();
    assertEquals("503-555-1000", loadedCall.getCaller());
    assertEquals("503-555-2000", loadedCall.getCallee());
  }

  private String fileNameFor(String customer) {
    String normalized = customer.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    if (normalized.isEmpty()) {
      normalized = "customer";
    }

    return "phonebill-" + normalized + ".txt";
  }
}