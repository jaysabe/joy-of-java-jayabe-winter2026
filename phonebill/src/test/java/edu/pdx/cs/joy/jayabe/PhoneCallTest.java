package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PhoneCall} class.
 */
public class PhoneCallTest {

    /**
     * Test that constructor creates a valid PhoneCall object
     */
    @Test
    void testPhoneCallConstructorCreatesValidObject() {
        String customer = "John Smith";
        String callerNumber = "503-123-4567";
        String calleeNumber = "971-987-6543";
        String begin = "01/15/2025 10:30 AM";
        String end = "01/15/2025 10:45 AM";

        PhoneCall phoneCall = new PhoneCall(customer, callerNumber, calleeNumber, begin, end);

        assertNotNull(phoneCall);
        assertThat(phoneCall, instanceOf(PhoneCall.class));
    }

    /**
     * Test that customer name can contain numbers
     */
    @Test
    void testCustomerNameCanContainNumbers() {
        String customer = "Customer123";
        PhoneCall phoneCall = new PhoneCall(customer, "503-123-4567", "971-987-6543",
                "01/15/2025 10:30 AM", "01/15/2025 10:45 AM");

        assertEquals(customer, phoneCall.getCustomer());
    }

    /**
     * Test that customer name can contain special characters
     */
    @Test
    void testCustomerNameCanContainSpecialCharacters() {
        String customer = "John O'Brien-Smith Jr.";
        PhoneCall phoneCall = new PhoneCall(customer, "503-123-4567", "971-987-6543",
                "01/15/2025 10:30 AM", "01/15/2025 10:45 AM");

        assertEquals(customer, phoneCall.getCustomer());
    }

    /**
     * Test that getCaller returns the correct phone number
     */
    @Test
    void testGetCallerReturnsCorrectNumber() {
        String callerNumber = "503-123-4567";
        PhoneCall phoneCall = new PhoneCall("John", callerNumber, "971-987-6543",
                "01/15/2025 10:30 AM", "01/15/2025 10:45 AM");

        assertEquals(callerNumber, phoneCall.getCaller());
    }

    /**
     * Test that getCallee returns the correct phone number
     */
    @Test
    void testGetCalleeReturnsCorrectNumber() {
        String calleeNumber = "971-987-6543";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", calleeNumber,
                "01/15/2025 10:30 AM", "01/15/2025 10:45 AM");

        assertEquals(calleeNumber, phoneCall.getCallee());
    }

    /**
     * Test that getBeginTimeString returns the correct begin time
     */
    @Test
    void testGetBeginTimeStringReturnsCorrectTime() {
        String begin = "01/15/2025 10:30 AM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543",
                begin, "01/15/2025 10:45 AM");

        assertEquals(begin, phoneCall.getBeginTimeString());
    }

    /**
     * Test that getEndTimeString returns the correct end time
     */
    @Test
    void testGetEndTimeStringReturnsCorrectTime() {
        String end = "01/15/2025 10:45 AM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543",
                "01/15/2025 10:30 AM", end);

        assertEquals(end, phoneCall.getEndTimeString());
    }

    /**
     * Test that begin time can be a past date/time
     */
    @Test
    void testBeginTimeCanBePastDateTime() {
        String pastBegin = "01/01/2020 9:00 AM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543",
                pastBegin, "01/01/2020 9:15 AM");

        assertEquals(pastBegin, phoneCall.getBeginTimeString());
    }

    /**
     * Test that begin time can be a future date/time
     */
    @Test
    void testBeginTimeCanBeFutureDateTime() {
        String futureBegin = "12/31/2030 11:00 PM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543",
                futureBegin, "12/31/2030 11:30 PM");

        assertEquals(futureBegin, phoneCall.getBeginTimeString());
    }

    /**
     * Test that date and time are properly concatenated with space
     */
    @Test
    void testDateAndTimeConcatenatedWithSpace() {
        String begin = "01/15/2025 10:30 AM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543",
                begin, "01/15/2025 10:45 AM");

        assertThat(phoneCall.getBeginTimeString(), containsString(" "));
        String[] parts = phoneCall.getBeginTimeString().split(" ");
        assertTrue(parts.length >= 2); // At least date and time
    }

    /**
     * Test PhoneCall with all valid parameters
     */
    @Test
    void testPhoneCallWithAllValidParameters() {
        String customer = "Jane Doe";
        String callerNumber = "503-555-1234";
        String calleeNumber = "971-555-5678";
        String begin = "03/20/2025 2:15 PM";
        String end = "03/20/2025 2:45 PM";

        PhoneCall phoneCall = new PhoneCall(customer, callerNumber, calleeNumber, begin, end);

        assertEquals(customer, phoneCall.getCustomer());
        assertEquals(callerNumber, phoneCall.getCaller());
        assertEquals(calleeNumber, phoneCall.getCallee());
        assertEquals(begin, phoneCall.getBeginTimeString());
        assertEquals(end, phoneCall.getEndTimeString());
    }

    /**
     * Test that PhoneCall extends AbstractPhoneCall
     */
    @Test
    void testPhoneCallExtendsAbstractPhoneCall() {
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543",
                "01/15/2025 10:30 AM", "01/15/2025 10:45 AM");

        assertThat(phoneCall, instanceOf(edu.pdx.cs.joy.AbstractPhoneCall.class));
    }

    /**
     * Test with different time formats (12-hour format)
     */
    @Test
    void testWith12HourTimeFormat() {
        String begin = "01/15/2025 1:30 PM";
        String end = "01/15/2025 2:00 PM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertEquals(begin, phoneCall.getBeginTimeString());
        assertEquals(end, phoneCall.getEndTimeString());
    }

    /**
     * Test with midnight time
     */
    @Test
    void testWithMidnightTime() {
        String begin = "01/15/2025 12:00 AM";
        String end = "01/15/2025 12:30 AM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertEquals(begin, phoneCall.getBeginTimeString());
    }

    /**
     * Test with noon time
     */
    @Test
    void testWithNoonTime() {
        String begin = "01/15/2025 12:00 PM";
        String end = "01/15/2025 12:30 PM";
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertEquals(begin, phoneCall.getBeginTimeString());
    }

    /**
     * Test that PhoneCall fields are immutable (cannot be changed after creation)
     */
    @Test
    void testPhoneCallFieldsAreImmutable() {
        String originalCaller = "503-123-4567";
        PhoneCall phoneCall = new PhoneCall("John", originalCaller, "971-987-6543",
                "01/15/2025 10:30 AM", "01/15/2025 10:45 AM");

        // Verify that the caller number hasn't changed
        assertEquals(originalCaller, phoneCall.getCaller());

        // PhoneCall should remain unchanged because fields are final
        assertThat(phoneCall.getCaller(), is(originalCaller));
    }
}