package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.Test;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;

/**
 * A unit test for the {@link PhoneBillServlet}.  It uses mockito to
 * provide mock http requests and responses.
 */
class PhoneBillServletTest {

  private static final String CUSTOMER = "Dave";
  private static final String CALLER = "503-245-2345";
  private static final String CALLEE = "765-389-1273";
  private static final String BEGIN = "02/27/2026 8:56 AM";
  private static final String END = "02/27/2026 10:27 AM";

  @Test
  void getWithoutCustomerReturnsPreconditionFailed() throws ServletException, IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    servlet.doGet(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_PRECONDITION_FAILED), contains("customer"));
  }

  @Test
  void addPhoneCallAndFetchCustomerBill() throws ServletException, IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(PhoneBillServlet.CUSTOMER_PARAMETER)).thenReturn(CUSTOMER);
    when(request.getParameter(PhoneBillServlet.CALLER_NUMBER_PARAMETER)).thenReturn(CALLER);
    when(request.getParameter(PhoneBillServlet.CALLEE_NUMBER_PARAMETER)).thenReturn(CALLEE);
    when(request.getParameter(PhoneBillServlet.BEGIN_PARAMETER)).thenReturn(BEGIN);
    when(request.getParameter(PhoneBillServlet.END_PARAMETER)).thenReturn(END);

    HttpServletResponse response = mock(HttpServletResponse.class);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);

    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    assertThat(stringWriter.toString(), containsString(Messages.addedPhoneCallForCustomer(CUSTOMER)));
    verify(response).setStatus(HttpServletResponse.SC_OK);

    HttpServletRequest getRequest = mock(HttpServletRequest.class);
    when(getRequest.getParameter(PhoneBillServlet.CUSTOMER_PARAMETER)).thenReturn(CUSTOMER);

    HttpServletResponse getResponse = mock(HttpServletResponse.class);
    StringWriter getBody = new StringWriter();
    when(getResponse.getWriter()).thenReturn(new PrintWriter(getBody, true));

    servlet.doGet(getRequest, getResponse);

    assertThat(getBody.toString(), containsString(CALLER));
    assertThat(getBody.toString(), containsString(CALLEE));
    verify(getResponse).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  void getWithBeginAndEndFiltersCallsByStartTime() throws ServletException, IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    addCall(servlet, CUSTOMER, CALLER, CALLEE, "03/01/2026 9:00 AM", "03/01/2026 9:30 AM");
    addCall(servlet, CUSTOMER, "503-222-1000", "503-222-2000", "03/15/2026 1:00 PM", "03/15/2026 1:20 PM");

    HttpServletRequest searchRequest = mock(HttpServletRequest.class);
    when(searchRequest.getParameter(PhoneBillServlet.CUSTOMER_PARAMETER)).thenReturn(CUSTOMER);
    when(searchRequest.getParameter(PhoneBillServlet.BEGIN_PARAMETER)).thenReturn("03/10/2026 12:00 AM");
    when(searchRequest.getParameter(PhoneBillServlet.END_PARAMETER)).thenReturn("03/31/2026 11:59 PM");

    HttpServletResponse searchResponse = mock(HttpServletResponse.class);
    StringWriter body = new StringWriter();
    when(searchResponse.getWriter()).thenReturn(new PrintWriter(body, true));

    servlet.doGet(searchRequest, searchResponse);

    assertThat(body.toString(), containsString("503-222-1000"));
    assertThat(body.toString(), containsString("503-222-2000"));
  }

  @Test
  void getWithOnlyBeginReturnsPreconditionFailed() throws IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(PhoneBillServlet.CUSTOMER_PARAMETER)).thenReturn(CUSTOMER);
    when(request.getParameter(PhoneBillServlet.BEGIN_PARAMETER)).thenReturn("03/01/2026 12:00 AM");

    HttpServletResponse response = mock(HttpServletResponse.class);

    servlet.doGet(request, response);

    verify(response).sendError(eq(HttpServletResponse.SC_PRECONDITION_FAILED), contains(PhoneBillServlet.END_PARAMETER));
  }

  @Test
  void postWithInvalidDateReturnsPreconditionFailed() throws IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    HttpServletRequest postRequest = mock(HttpServletRequest.class);
    when(postRequest.getParameter(PhoneBillServlet.CUSTOMER_PARAMETER)).thenReturn(CUSTOMER);
    when(postRequest.getParameter(PhoneBillServlet.CALLER_NUMBER_PARAMETER)).thenReturn(CALLER);
    when(postRequest.getParameter(PhoneBillServlet.CALLEE_NUMBER_PARAMETER)).thenReturn(CALLEE);
    when(postRequest.getParameter(PhoneBillServlet.BEGIN_PARAMETER)).thenReturn("not-a-date");
    when(postRequest.getParameter(PhoneBillServlet.END_PARAMETER)).thenReturn(END);

    HttpServletResponse response = mock(HttpServletResponse.class);

    servlet.doPost(postRequest, response);

    verify(response).sendError(eq(HttpServletResponse.SC_PRECONDITION_FAILED), contains("Invalid date/time format"));
  }

  private void addCall(PhoneBillServlet servlet, String customer, String caller, String callee, String begin, String end)
    throws IOException {
    HttpServletRequest postRequest = mock(HttpServletRequest.class);
    when(postRequest.getParameter(PhoneBillServlet.CUSTOMER_PARAMETER)).thenReturn(customer);
    when(postRequest.getParameter(PhoneBillServlet.CALLER_NUMBER_PARAMETER)).thenReturn(caller);
    when(postRequest.getParameter(PhoneBillServlet.CALLEE_NUMBER_PARAMETER)).thenReturn(callee);
    when(postRequest.getParameter(PhoneBillServlet.BEGIN_PARAMETER)).thenReturn(begin);
    when(postRequest.getParameter(PhoneBillServlet.END_PARAMETER)).thenReturn(end);

    HttpServletResponse postResponse = mock(HttpServletResponse.class);
    when(postResponse.getWriter()).thenReturn(new PrintWriter(new StringWriter(), true));

    servlet.doPost(postRequest, postResponse);
  }

}
