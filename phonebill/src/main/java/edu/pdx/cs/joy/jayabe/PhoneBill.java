package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneBill;
import java.util.ArrayList;
import java.util.Collection;

public class PhoneBill extends AbstractPhoneBill<PhoneCall> {
  private final String customer;
  private final Collection<PhoneCall> phonesCalls;

  public PhoneBill(String customer) {
    this.customer = customer;
    this.phonesCalls = new ArrayList<>();
  }

  @Override
  public String getCustomer() {
    return this.customer;
  }

  /**
   * Adds a phone call to this customer's phone bill
   * @param creates and inserts a phone call
   * */
  @Override
  public void addPhoneCall(PhoneCall call) {
    this.phonesCalls.add(call);
  }

  /**
   * Returns all of the phone calls from one customer
   * @return A collection of phone calls
   * */
  @Override
  public Collection<PhoneCall> getPhoneCalls() {
    return this.phonesCalls;
  }


}
