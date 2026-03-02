package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import edu.pdx.cs.joy.web.HttpRequestHelper.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneBillRestClientTest {

  @Test
  void getPhoneBillPerformsHttpGetWithCustomerParameter() throws ParserException, IOException {
    String customer = "Dave";
    String content = "503-245-2345|765-389-1273|02/27/2026 8:56 AM|02/27/2026 10:27 AM\n";

    HttpRequestHelper http = mock(HttpRequestHelper.class);
    when(http.get(eq(Map.of(PhoneBillServlet.CUSTOMER_PARAMETER, customer)))).thenReturn(new Response(content));

    PhoneBillRestClient client = new PhoneBillRestClient(http);
    List<PhoneCallRecord> calls = client.getPhoneBill(customer);

    assertThat(calls.size(), equalTo(1));
    assertThat(calls.get(0).getCallerNumber(), equalTo("503-245-2345"));
  }

  @Test
  void searchCallsPerformsHttpGetWithCustomerAndDateRange() throws IOException, ParserException {
    String customer = "Dave";
    String begin = "03/01/2026 12:00 AM";
    String end = "03/31/2026 11:59 PM";

    HttpRequestHelper http = mock(HttpRequestHelper.class);
    when(http.get(eq(Map.of(
      PhoneBillServlet.CUSTOMER_PARAMETER, customer,
      PhoneBillServlet.BEGIN_PARAMETER, begin,
      PhoneBillServlet.END_PARAMETER, end
    )))).thenReturn(new Response(""));

    PhoneBillRestClient client = new PhoneBillRestClient(http);
    client.searchPhoneCalls(customer, begin, end);
  }
}
