package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.MethodOrderer.MethodName;

/**
 * Tests the {@link Project4} class by invoking its main method with various arguments
 */
@TestMethodOrder(MethodName.class)
class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");

    @Test
        void test0RemoveAllMappings() throws IOException {
      PhoneBillRestClient client = new PhoneBillRestClient(HOSTNAME, Integer.parseInt(PORT));
            client.removeAllPhoneBills();
    }

    @Test
    void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getTextWrittenToStandardError(), containsString(Project5.MISSING_ARGS));
    }

    @Test
    void test1ReadmeOptionPrintsReadmeAndExits() {
      MainMethodResult result = invokeMain(Project4.class, "-README");

      assertThat(result.getTextWrittenToStandardError(), equalTo(""));
      assertThat(result.getTextWrittenToStandardOut(), containsString("Project 5"));
    }

    @Test
    void test1HostWithoutPortIsAnError() {
      MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "Dave");
      assertThat(result.getTextWrittenToStandardError(), containsString("Host and port must be specified together"));
    }

    @Test
    void test2SearchEmptyCustomerBill() {
        MainMethodResult result = invokeMain(Project4.class,
          "-host", HOSTNAME,
          "-port", PORT,
          "-search",
          "Dave");

        assertThat(result.getTextWrittenToStandardError(), equalTo(""));

        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString("Customer: Dave"));
    }

      @Test
      void test2SearchRangeWithNoMatchesPrintsMessage() {
        MainMethodResult result = invokeMain(Project4.class,
          "-host", HOSTNAME,
          "-port", PORT,
          "-search",
          "Dave",
          "03/01/2027", "12:00", "AM",
          "03/31/2027", "11:59", "PM");

        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString("No phone calls found in the specified range"));
      }

      @Test
      void test2SearchDoesNotAllowPrintOption() {
        MainMethodResult result = invokeMain(Project4.class,
          "-host", HOSTNAME,
          "-port", PORT,
          "-search",
          "-print",
          "Dave");

        assertThat(result.getTextWrittenToStandardError(), containsString("-print is not supported with -search"));
      }

  @Test
  void test2InvalidDateFormatIsReported() {
    MainMethodResult result = invokeMain(Project4.class,
      "-host", HOSTNAME,
      "-port", PORT,
      "Dave",
      "503-245-2345",
      "765-389-1273",
      "02/27/2026", "25:61", "AM",
      "02/27/2026", "10:27", "AM");

    assertThat(result.getTextWrittenToStandardError(), containsString("Invalid date/time format"));
  }

    @Test
    void test3AddCallAndSearch() {
        MainMethodResult result = invokeMain(Project4.class,
          "-host", HOSTNAME,
          "-port", PORT,
          "Dave",
          "503-245-2345",
          "765-389-1273",
          "02/27/2026", "8:56", "AM",
          "02/27/2026", "10:27", "AM");

        assertThat(result.getTextWrittenToStandardError(), equalTo(""));

        result = invokeMain(Project4.class,
          "-host", HOSTNAME,
          "-port", PORT,
          "-search",
          "Dave");

        assertThat(result.getTextWrittenToStandardError(), equalTo(""));

        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString("503-245-2345"));
    }
}