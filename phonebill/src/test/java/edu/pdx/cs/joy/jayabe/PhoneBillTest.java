package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

        LocalDateTime begin = LocalDateTime.of(2021, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2021, 10, 10, 10, 25);

        PhoneCall call = new PhoneCall("Frank", "360-910-6767", "503-830-0138", begin, end);
        phoneBill.addPhoneCall(call);

        Collection<PhoneCall> calls = phoneBill.getPhoneCalls();

        // Use Collection assertions
        assertEquals(1, calls.size());
        assertTrue(calls.contains(call), "The phone bill should contain the added call");
    }

    @Test
    public void testAddMultiplePhoneCalls() {
        PhoneBill phoneBill = new PhoneBill("Frank");

        LocalDateTime begin1 = LocalDateTime.of(2021, 10, 10, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2021, 10, 10, 10, 25);
        PhoneCall call1 = new PhoneCall("Frank", "360-910-6767", "503-830-0138", begin1, end1);

        LocalDateTime begin2 = LocalDateTime.of(2021, 10, 11, 11, 0);
        LocalDateTime end2 = LocalDateTime.of(2021, 10, 11, 11, 45);
        PhoneCall call2 = new PhoneCall("Frank", "360-910-6767", "503-830-0138", begin2, end2);

        phoneBill.addPhoneCall(call1);
        phoneBill.addPhoneCall(call2);

        Collection<PhoneCall> calls = phoneBill.getPhoneCalls();

        assertEquals(2, calls.size());
        assertTrue(calls.contains(call1));
        assertTrue(calls.contains(call2));
    }
}
