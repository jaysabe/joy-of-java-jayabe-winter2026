package edu.pdx.cs.joy.jayabe;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import edu.pdx.cs.joy.web.HttpRequestHelper.Response;
import edu.pdx.cs.joy.web.HttpRequestHelper.RestException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Client for accessing the Phone Bill REST service.
 */
public class PhoneBillRestClient {

  private static final String WEB_APP = "phonebill";
  private static final String SERVLET = "calls";

  private final HttpRequestHelper http;

  /**
   * Creates a client for a Phone Bill REST service running on the given host and port.
   *
   * @param hostName Host name
   * @param port Port number
   */
  public PhoneBillRestClient(String hostName, int port) {
    this(new HttpRequestHelper(String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET)));
  }

  /**
   * Creates a client with an injected HTTP helper for tests.
   *
   * @param http HTTP helper to use
   */
  @VisibleForTesting
  PhoneBillRestClient(HttpRequestHelper http) {
    this.http = http;
  }

  /**
   * Retrieves all calls for a customer.
   *
   * @param customer Customer name
   * @return Calls for the customer
   * @throws IOException If HTTP communication fails
   * @throws ParserException If response content cannot be parsed
   */
  public List<PhoneCallRecord> getPhoneBill(String customer) throws IOException, ParserException {
    Response response = http.get(Map.of(PhoneBillServlet.CUSTOMER_PARAMETER, customer));
    throwExceptionIfNotOkayHttpStatus(response);

    TextParser parser = new TextParser(new StringReader(response.getContent()));
    return parser.parsePhoneCalls();
  }

  /**
   * Searches calls for a customer, optionally limited to an inclusive begin-time range.
   *
   * @param customer Customer name
   * @param begin Inclusive range begin, or {@code null}
   * @param end Inclusive range end, or {@code null}
   * @return Matching calls
   * @throws IOException If HTTP communication fails
   * @throws ParserException If response content cannot be parsed
   */
  public List<PhoneCallRecord> searchPhoneCalls(String customer, String begin, String end) throws IOException, ParserException {
    if (begin == null && end == null) {
      return getPhoneBill(customer);
    }

    Response response = http.get(Map.of(
      PhoneBillServlet.CUSTOMER_PARAMETER, customer,
      PhoneBillServlet.BEGIN_PARAMETER, begin,
      PhoneBillServlet.END_PARAMETER, end));
    throwExceptionIfNotOkayHttpStatus(response);

    TextParser parser = new TextParser(new StringReader(response.getContent()));
    return parser.parsePhoneCalls();
  }

  /**
   * Adds one phone call for a customer.
   *
   * @param customer Customer name
   * @param callerNumber Caller phone number
   * @param calleeNumber Callee phone number
   * @param begin Begin date/time text
   * @param end End date/time text
   * @throws IOException If HTTP communication fails
   */
  public void addPhoneCall(String customer, String callerNumber, String calleeNumber, String begin, String end) throws IOException {
    Response response = http.post(Map.of(
      PhoneBillServlet.CUSTOMER_PARAMETER, customer,
      PhoneBillServlet.CALLER_NUMBER_PARAMETER, callerNumber,
      PhoneBillServlet.CALLEE_NUMBER_PARAMETER, calleeNumber,
      PhoneBillServlet.BEGIN_PARAMETER, begin,
      PhoneBillServlet.END_PARAMETER, end));
    throwExceptionIfNotOkayHttpStatus(response);
  }

  /**
   * Removes all stored phone bills on the server.
   *
   * @throws IOException If HTTP communication fails
   */
  public void removeAllPhoneBills() throws IOException {
    Response response = http.delete(Map.of());
    throwExceptionIfNotOkayHttpStatus(response);
  }

  /**
   * Throws a REST exception when the server status is not HTTP 200.
   *
   * @param response HTTP response to validate
   */
  private void throwExceptionIfNotOkayHttpStatus(Response response) {
    int code = response.getHttpStatusCode();
    if (code != HTTP_OK) {
      String message = response.getContent();
      throw new RestException(code, message);
    }
  }

}
