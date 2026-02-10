package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

class Project3IT extends InvokeMainTestCase {

    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project3.class, args);
    }

    @Test
    void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing customer information")
        );
    }

    @Test
    void testOneCommandLineArgument() {
        MainMethodResult result = invokeMain("Alice");
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing caller phone number")
        );
    }

    @Test
    void testTwoCommandLineArguments() {
        MainMethodResult result = invokeMain("Alice", "123-456-7890");
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing callee phone number")
        );
    }

    @Test
    void testThreeCommandLineArguments() {
        MainMethodResult result = invokeMain(
                "Alice",
                "123-456-7890",
                "234-567-8901"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing begin date")
        );
    }

    @Test
    void testFourCommandLineArguments() {
        MainMethodResult result = invokeMain(
                "Alice",
                "123-456-7890",
                "234-567-8901",
                "01/01/2020"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing begin time")
        );
    }

    @Test
    void testFiveCommandLineArguments() {
        MainMethodResult result = invokeMain(
                "Alice",
                "123-456-7890",
                "234-567-8901",
                "01/01/2020",
                "09:00"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing begin am/pm")
        );
    }

    @Test
    void testSixCommandLineArguments() {
        MainMethodResult result = invokeMain(
                "Alice",
                "123-456-7890",
                "234-567-8901",
                "01/01/2020",
                "09:00",
                "AM"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing end date")
        );
    }

    @Test
    void testSevenCommandLineArguments() {
        MainMethodResult result = invokeMain(
                "Alice",
                "123-456-7890",
                "234-567-8901",
                "01/01/2020",
                "09:00",
                "AM",
                "01/01/2020"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing end time")
        );
    }

    @Test
    void testEightCommandLineArguments() {
        MainMethodResult result = invokeMain(
                "Alice",
                "123-456-7890",
                "234-567-8901",
                "01/01/2020",
                "09:00",
                "AM",
                "01/01/2020",
                "10:00"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing end am/pm")
        );
    }

    @Test
    void testPrintOptionOutputsNewCall() {
        MainMethodResult result = invokeMain(
                "-print", "Alice", "123-456-7890", "234-567-8901",
                "01/01/2026", "09:00", "AM", "01/01/2026", "10:00", "AM"
        );
        assertThat(result.getTextWrittenToStandardOut(), containsString("Phone call from 123-456-7890 to 234-567-8901"));
    }

    @Test
    void testPrettyPrintToStandardOut() {
        MainMethodResult result = invokeMain(
                "-pretty", "-", "Alice", "123-456-7890", "234-567-8901",
                "01/01/2026", "09:00", "AM", "01/01/2026", "10:00", "AM"
        );
        String output = result.getTextWrittenToStandardOut();
        assertThat(output, containsString("Alice"));
        assertThat(output, containsString("123-456-7890"));
        assertThat(output, containsString("234-567-8901"));
    }

    @Test
    void testEndTimeBeforeBeginTime() {
        MainMethodResult result = invokeMain(
                "Alice", "123-456-7890", "234-567-8901",
                "01/01/2026", "10:00", "AM", "01/01/2026", "09:00", "AM"
        );
        assertThat(result.getTextWrittenToStandardError(), containsString("End time cannot be before begin time"));
    }

    @Test
    void testCustomerNameMismatchInFile(@org.junit.jupiter.api.io.TempDir java.io.File tempDir) throws java.io.IOException {
        java.io.File file = new java.io.File(tempDir, "mismatch.txt");

        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            pw.println("Alice");
        }

        // Attempt to run the program for "Bob" using Alice's file
        MainMethodResult result = invokeMain("-textFile", file.getAbsolutePath(), "Bob",
                "123-456-7890", "234-567-8901",
                "01/01/2026", "09:00", "AM", "01/01/2026", "10:00", "AM");

        assertThat(result.getTextWrittenToStandardError(), containsString("does not match command line"));
    }

    @Test
    void testREADMEFlag() {
        MainMethodResult result = invokeMain("-README");
        assertThat(result.getTextWrittenToStandardOut(), containsString("Project 3"));
    }
}
