package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper.RestException;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test that tests the REST calls made by {@link PhoneBillRestClient}
 */
@TestMethodOrder(MethodName.class)
class PhoneBillRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private PhoneBillRestClient newPhoneBillRestClient() {
    int port = Integer.parseInt(PORT);
    return new PhoneBillRestClient(HOSTNAME, port);
  }

  @Test
  void test0RemoveAllPhoneBills() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    client.removeAllPhoneBills();
  }

  @Test
  void test1EmptyServerContainsNoCallsForCustomer() throws IOException, ParserException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    List<PhoneCallRecord> calls = client.getPhoneBill("Dave");
    assertThat(calls.size(), equalTo(0));
  }

  @Test
  void test2AddOneCall() throws IOException, ParserException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    client.addPhoneCall("Dave", "503-245-2345", "765-389-1273", "02/27/2026 8:56 AM", "02/27/2026 10:27 AM");

    List<PhoneCallRecord> calls = client.getPhoneBill("Dave");
    assertThat(calls.size(), equalTo(1));
    assertThat(calls.get(0).getCallerNumber(), equalTo("503-245-2345"));
  }

  @Test
  void test4EmptyCustomerThrowsException() {
    PhoneBillRestClient client = newPhoneBillRestClient();
    String emptyString = "";

    RestException ex = assertThrows(RestException.class,
      () -> client.addPhoneCall(emptyString, "503-245-2345", "765-389-1273", "02/27/2026 8:56 AM", "02/27/2026 10:27 AM"));
    assertThat(ex.getHttpStatusCode(), equalTo(HttpURLConnection.HTTP_PRECON_FAILED));
    assertThat(ex.getMessage(), containsString(Messages.missingRequiredParameter(PhoneBillServlet.CUSTOMER_PARAMETER)));
  }

}
