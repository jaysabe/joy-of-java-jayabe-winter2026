package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

class Project1IT extends InvokeMainTestCase {

    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project1.class, args);
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
}
