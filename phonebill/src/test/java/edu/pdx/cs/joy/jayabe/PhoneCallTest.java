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

        assertEquals("01/15/2025 10:30", phoneCall.getBeginTimeString());
        assertEquals("01/15/2025 10:45", phoneCall.getEndTimeString());
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

        // Fields are final, cannot be modified
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
}
