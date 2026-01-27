package edu.pdx.cs.joy.jayabe;


import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class PhoneBillTest {
    @Test
    public void testPhoneBillConstructorWithValidCustomer() {
        String customer = "Frank";
        PhoneBill phoneBill = new PhoneBill(customer);
        assertThat(customer, is(not(nullValue())));
        assertThat(customer, is(not("")));
        assertEquals(customer, phoneBill.getCustomer());
    }

    @Test
    public void testAddOnePhoneCall() {
        PhoneBill phoneBill = new PhoneBill("Frank");
        PhoneCall call = new PhoneCall("Frank", "360-910-6767", "503-830-0138", "10/10/21 10:00AM", "10/10/21 10:25AM");
        phoneBill.addPhoneCall(call);
        assertEquals(1, phoneBill.getPhoneCalls().size());
    }


}
