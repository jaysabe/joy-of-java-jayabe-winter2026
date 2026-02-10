package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneBill;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The <code>PhoneBill</code> class represents a phone bill for a specific customer.
 * It extends {@link AbstractPhoneBill} and manages a collection of {@link PhoneCall} objects
 * associated with a single customer.
 *
 * <p>Each phone bill is identified by the customer's name and maintains a list of all
 * phone calls made by that customer. This class provides methods to retrieve the customer
 * name, add new phone calls, and access the complete collection of calls.
 *
 * <p>Example usage:
 * <pre>
 *   PhoneBill bill = new PhoneBill("John Doe");
 *   PhoneCall call = new PhoneCall(...);
 *   bill.addPhoneCall(call);
 *   Collection&lt;PhoneCall&gt; calls = bill.getPhoneCalls();
 * </pre>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class PhoneBill extends AbstractPhoneBill<PhoneCall> {
  /** The name of the customer */
  private final String customer;
  /**
   * A collection of phone calls associated with this customer's bill.
   */
  private final Collection<PhoneCall> phoneCalls;

  /**
   * Constructs a new <code>PhoneBill</code> for the specified customer.
   * The phone bill is initialized with an empty collection of phone calls.
   *
   * @param customer the name of the customer for this phone bill; must not be null
   */
  public PhoneBill(String customer) {
    this.customer = customer;
    this.phoneCalls = new ArrayList<>();
  }

  /**
   * @return customer
   * */
  @Override
  public String getCustomer() {
    return this.customer;
  }

  /**
   * Adds a phone call to this customer's phone bill
   * @param call creates and inserts a phone call
   * */
  @Override
  public void addPhoneCall(PhoneCall call) {
    this.phoneCalls.add(call);
  }

  /**
   * Returns all phone calls from one customer, sorted chronologically by begin time.
   * If two phone calls begin at the same time, they are sorted by caller phone number.
   * @return A collection of phone calls in sorted order
   * */
  @Override
  public Collection<PhoneCall> getPhoneCalls() {
    ArrayList<PhoneCall> sortedCalls = new ArrayList<>(this.phoneCalls);
    Collections.sort(sortedCalls);
    return sortedCalls;
  }


}
