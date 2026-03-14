package com.example.phonebill_android;

import edu.pdx.cs.joy.AbstractPhoneBill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PhoneBill extends AbstractPhoneBill<PhoneCall> {
  private final String customer;
  private final Collection<PhoneCall> phoneCalls;

  public PhoneBill(String customer) {
    this.customer = customer;
    this.phoneCalls = new ArrayList<>();
  }

  @Override
  public String getCustomer() {
    return this.customer;
  }

  @Override
  public void addPhoneCall(PhoneCall call) {
    this.phoneCalls.add(call);
  }

  @Override
  public Collection<PhoneCall> getPhoneCalls() {
    ArrayList<PhoneCall> sortedCalls = new ArrayList<>(this.phoneCalls);
    Collections.sort(sortedCalls);
    return sortedCalls;
  }
}
