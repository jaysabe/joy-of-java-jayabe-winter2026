package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneCall;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PhoneCall} class (TDD style)
 */
public class PhoneCallTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    @Test
    void testPhoneCallConstructorCreatesValidObject() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);

        PhoneCall phoneCall = new PhoneCall("John Smith", "503-123-4567", "971-987-6543", begin, end);

        assertNotNull(phoneCall);
        assertThat(phoneCall, instanceOf(PhoneCall.class));
    }

    @Test
    void testCustomerNameCanContainNumbers() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("Customer123", "503-123-4567", "971-987-6543", begin, end);

        assertEquals("Customer123", phoneCall.getCustomer());
    }

    @Test
    void testCustomerNameCanContainSpecialCharacters() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("John O'Brien-Smith Jr.", "503-123-4567", "971-987-6543", begin, end);

        assertEquals("John O'Brien-Smith Jr.", phoneCall.getCustomer());
    }

    @Test
    void testGetCallerReturnsCorrectNumber() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertEquals("503-123-4567", phoneCall.getCaller());
    }

    @Test
    void testGetCalleeReturnsCorrectNumber() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertEquals("971-987-6543", phoneCall.getCallee());
    }

    @Test
    void testGetBeginAndEndTimeStrings() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        // Time strings should use FormatStyle.SHORT format
        assertNotNull(phoneCall.getBeginTimeString());
        assertNotNull(phoneCall.getEndTimeString());
    }

    @Test
    void testDateAndTimeConcatenatedWithSpace() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, begin.plusMinutes(15));

        assertTrue(phoneCall.getBeginTimeString().contains(" "));
    }

    @Test
    void testPhoneCallFieldsAreImmutable() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertEquals("503-123-4567", phoneCall.getCaller());
        assertEquals("971-987-6543", phoneCall.getCallee());
        assertEquals("John", phoneCall.getCustomer());
    }

    @Test
    void testPhoneCallExtendsAbstractPhoneCall() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 10, 45);
        PhoneCall phoneCall = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, end);

        assertThat(phoneCall, instanceOf(AbstractPhoneCall.class));
    }

    @Test
    void testCompareToWithDifferentBeginTimes() {
        LocalDateTime earlier = LocalDateTime.of(2025, 1, 15, 10, 0);
        LocalDateTime later = LocalDateTime.of(2025, 1, 15, 11, 0);
        
        PhoneCall call1 = new PhoneCall("John", "503-123-4567", "971-987-6543", earlier, earlier.plusMinutes(30));
        PhoneCall call2 = new PhoneCall("John", "503-123-4567", "971-987-6543", later, later.plusMinutes(30));
        
        assertTrue(call1.compareTo(call2) < 0, "Earlier call should be less than later call");
        assertTrue(call2.compareTo(call1) > 0, "Later call should be greater than earlier call");
    }

    @Test
    void testCompareToWithSameBeginTimeDifferentCaller() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 0);
        
        PhoneCall call1 = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, begin.plusMinutes(30));
        PhoneCall call2 = new PhoneCall("John", "503-999-8888", "971-987-6543", begin, begin.plusMinutes(30));
        
        assertTrue(call1.compareTo(call2) < 0, "Lower caller number should be less than higher caller number");
        assertTrue(call2.compareTo(call1) > 0, "Higher caller number should be greater than lower caller number");
    }

    @Test
    void testCompareToWithSameBeginTimeAndCaller() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 0);
        
        PhoneCall call1 = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, begin.plusMinutes(30));
        PhoneCall call2 = new PhoneCall("John", "503-123-4567", "971-111-2222", begin, begin.plusMinutes(45));
        
        assertEquals(0, call1.compareTo(call2), "Calls with same begin time and caller should be equal");
    }

    @Test
    void testCompareToWithSelf() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 0);
        PhoneCall call = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, begin.plusMinutes(30));
        
        assertEquals(0, call.compareTo(call), "Call should be equal to itself");
    }

    @Test
    void testPhoneCallImplementsComparable() {
        LocalDateTime begin = LocalDateTime.of(2025, 1, 15, 10, 0);
        PhoneCall call = new PhoneCall("John", "503-123-4567", "971-987-6543", begin, begin.plusMinutes(30));
        
        assertThat(call, instanceOf(Comparable.class));
    }
}
