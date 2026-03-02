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
 * A helper class for accessing the phone bill REST service.
 */
public class PhoneBillRestClient {

    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

  private final HttpRequestHelper http;


    /**
     * Creates a client to the Phone Bil REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public PhoneBillRestClient( String hostName, int port )
    {
      this(new HttpRequestHelper(String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET)));
    }

  @VisibleForTesting
  PhoneBillRestClient(HttpRequestHelper http) {
    this.http = http;
  }

  public List<PhoneCallRecord> getPhoneBill(String customer) throws IOException, ParserException {
    Response response = http.get(Map.of(PhoneBillServlet.CUSTOMER_PARAMETER, customer));
    throwExceptionIfNotOkayHttpStatus(response);

    TextParser parser = new TextParser(new StringReader(response.getContent()));
    return parser.parsePhoneCalls();
  }

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

    public void addPhoneCall(String customer, String callerNumber, String calleeNumber, String begin, String end) throws IOException {
      Response response = http.post(Map.of(
        PhoneBillServlet.CUSTOMER_PARAMETER, customer,
        PhoneBillServlet.CALLER_NUMBER_PARAMETER, callerNumber,
        PhoneBillServlet.CALLEE_NUMBER_PARAMETER, calleeNumber,
        PhoneBillServlet.BEGIN_PARAMETER, begin,
        PhoneBillServlet.END_PARAMETER, end));
      throwExceptionIfNotOkayHttpStatus(response);
    }

  public void removeAllPhoneBills() throws IOException {
      Response response = http.delete(Map.of());
      throwExceptionIfNotOkayHttpStatus(response);
    }

    private void throwExceptionIfNotOkayHttpStatus(Response response) {
      int code = response.getHttpStatusCode();
      if (code != HTTP_OK) {
        String message = response.getContent();
        throw new RestException(code, message);
      }
    }

}
