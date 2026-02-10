package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

class Project2IT extends InvokeMainTestCase {

    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project2.class, args);
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
                containsString("missing end date")
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
                "01/01/2020"
        );
        assertThat(
                result.getTextWrittenToStandardError().toLowerCase(),
                containsString("missing end time")
        );
    }

    @Test
    void testPrintOptionOutputsNewCall() {
        MainMethodResult result = invokeMain(
                "-print", "Alice", "123-456-7890", "234-567-8901",
                "01/01/2026", "09:00", "01/01/2026", "10:00"
        );
        assertThat(result.getTextWrittenToStandardOut(), containsString("Phone call from 123-456-7890 to 234-567-8901"));
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
                "01/01/2026", "09:00", "01/01/2026", "10:00");

        assertThat(result.getTextWrittenToStandardError(), containsString("does not match command line"));
    }


}
