package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class PrettyPrinterTest {

    @Test
    void testPrettyPrintEmptyPhoneBill() throws IOException {
        PhoneBill bill = new PhoneBill("Alice");
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(bill);
        
        String output = sw.toString();
        assertThat(output, containsString("Alice"));
        assertThat(output, containsString("No phone calls"));
    }

    @Test
    void testPrettyPrintPhoneBillWithOneCall() throws IOException {
        PhoneBill bill = new PhoneBill("Alice");
        LocalDateTime begin = LocalDateTime.of(2026, 1, 27, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 27, 10, 30);
        PhoneCall call = new PhoneCall("Alice", "503-123-4567", "503-765-4321", begin, end);
        bill.addPhoneCall(call);
        
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(bill);
        
        String output = sw.toString();
        assertThat(output, containsString("Alice"));
        assertThat(output, containsString("503-123-4567"));
        assertThat(output, containsString("503-765-4321"));
        assertThat(output, containsString("Duration"));
        assertThat(output, containsString("30 minutes"));
    }

    @Test
    void testPrettyPrintPhoneBillWithMultipleCalls() throws IOException {
        PhoneBill bill = new PhoneBill("Alice");
        
        LocalDateTime begin1 = LocalDateTime.of(2026, 1, 27, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2026, 1, 27, 10, 30);
        PhoneCall call1 = new PhoneCall("Alice", "503-123-4567", "503-765-4321", begin1, end1);
        
        LocalDateTime begin2 = LocalDateTime.of(2026, 1, 28, 14, 0);
        LocalDateTime end2 = LocalDateTime.of(2026, 1, 28, 14, 15);
        PhoneCall call2 = new PhoneCall("Alice", "503-123-4567", "503-999-8888", begin2, end2);
        
        bill.addPhoneCall(call1);
        bill.addPhoneCall(call2);
        
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(bill);
        
        String output = sw.toString();
        assertThat(output, containsString("Alice"));
        assertThat(output, containsString("503-123-4567"));
        assertThat(output, containsString("503-765-4321"));
        assertThat(output, containsString("503-999-8888"));
        assertThat(output, containsString("30 minutes"));
        assertThat(output, containsString("15 minutes"));
    }

    @Test
    void testPrettyPrintShowsCallsSorted() throws IOException {
        PhoneBill bill = new PhoneBill("Alice");
        
        // Add calls in reverse chronological order
        LocalDateTime begin2 = LocalDateTime.of(2026, 1, 28, 14, 0);
        LocalDateTime end2 = LocalDateTime.of(2026, 1, 28, 14, 15);
        PhoneCall call2 = new PhoneCall("Alice", "503-999-8888", "503-111-2222", begin2, end2);
        
        LocalDateTime begin1 = LocalDateTime.of(2026, 1, 27, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2026, 1, 27, 10, 30);
        PhoneCall call1 = new PhoneCall("Alice", "503-123-4567", "503-765-4321", begin1, end1);
        
        bill.addPhoneCall(call2);  // Add later call first
        bill.addPhoneCall(call1);  // Add earlier call second
        
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(bill);
        
        String output = sw.toString();
        // Verify that call1 appears before call2 in the output
        int indexOfCall1 = output.indexOf("503-123-4567");
        int indexOfCall2 = output.indexOf("503-999-8888");
        assertTrue(indexOfCall1 < indexOfCall2, "Calls should be sorted chronologically");
    }

    @Test
    void testPrettyPrintWithOneMinuteCall() throws IOException {
        PhoneBill bill = new PhoneBill("Bob");
        LocalDateTime begin = LocalDateTime.of(2026, 1, 27, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 27, 10, 1);
        PhoneCall call = new PhoneCall("Bob", "503-123-4567", "503-765-4321", begin, end);
        bill.addPhoneCall(call);
        
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(bill);
        
        String output = sw.toString();
        assertThat(output, containsString("1 minute"));
        assertTrue(output.contains("1 minute") && !output.contains("1 minutes"),
                "Should use singular 'minute' for 1-minute duration");
    }

    @Test
    void testPrettyPrintNullBill() throws IOException {
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(null);
        
        String output = sw.toString();
        assertEquals("", output, "Should produce empty output for null bill");
    }

    @Test
    void testPrettyPrintFormatsHeader() throws IOException {
        PhoneBill bill = new PhoneBill("Charlie");
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(sw);
        
        printer.dump(bill);
        
        String output = sw.toString();
        assertThat(output, containsString("Phone Bill"));
        assertThat(output, containsString("Charlie"));
        assertThat(output, containsString("==")); // Header separator
    }
}
