package edu.pdx.cs.joy.jayabe;

import com.google.common.annotations.VisibleForTesting;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>PhoneBill</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple dictionary of words
 * and their definitions.
 */
public class PhoneBillServlet extends HttpServlet
{
    static final String CUSTOMER_PARAMETER = "customer";
    static final String CALLER_NUMBER_PARAMETER = "callerNumber";
    static final String CALLEE_NUMBER_PARAMETER = "calleeNumber";
    static final String BEGIN_PARAMETER = "begin";
    static final String END_PARAMETER = "end";

    private final Map<String, List<PhoneCallRecord>> phoneBills = new HashMap<>();

    /**
     * Handles an HTTP GET request from a client by writing the definition of the
     * word specified in the "word" HTTP parameter to the HTTP response.  If the
     * "word" parameter is not specified, all of the entries in the dictionary
     * are written to the HTTP response.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        response.setContentType( "text/plain" );

        String customer = getParameter(CUSTOMER_PARAMETER, request);
        if (customer == null) {
            missingRequiredParameter(response, CUSTOMER_PARAMETER);
            return;
        }

        String begin = getParameter(BEGIN_PARAMETER, request);
        String end = getParameter(END_PARAMETER, request);

        if ((begin == null) != (end == null)) {
            missingRequiredParameter(response, begin == null ? BEGIN_PARAMETER : END_PARAMETER);
            return;
        }

        List<PhoneCallRecord> allCalls = this.phoneBills.getOrDefault(customer, List.of());
        if (begin == null) {
            writeCalls(response, new ArrayList<>(allCalls));
            return;
        }

        try {
            LocalDateTime beginTime = LocalDateTime.parse(begin, PhoneCallRecord.DATE_TIME_FORMAT);
            LocalDateTime endTime = LocalDateTime.parse(end, PhoneCallRecord.DATE_TIME_FORMAT);

            List<PhoneCallRecord> filtered = allCalls.stream()
              .filter(call -> call.beginsBetween(beginTime, endTime))
              .collect(Collectors.toList());

            writeCalls(response, filtered);

        } catch (DateTimeParseException ex) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid date/time format");
        }
    }

    /**
     * Handles an HTTP POST request by storing the dictionary entry for the
     * "word" and "definition" request parameters.  It writes the dictionary
     * entry to the HTTP response.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        response.setContentType( "text/plain" );

                String customer = getParameter(CUSTOMER_PARAMETER, request);
                if (customer == null) {
                        missingRequiredParameter(response, CUSTOMER_PARAMETER);
            return;
        }

                String callerNumber = getParameter(CALLER_NUMBER_PARAMETER, request);
                if (callerNumber == null) {
                    missingRequiredParameter(response, CALLER_NUMBER_PARAMETER);
                    return;
                }

                String calleeNumber = getParameter(CALLEE_NUMBER_PARAMETER, request);
                if (calleeNumber == null) {
                    missingRequiredParameter(response, CALLEE_NUMBER_PARAMETER);
                    return;
                }

                String begin = getParameter(BEGIN_PARAMETER, request);
                if (begin == null) {
                    missingRequiredParameter(response, BEGIN_PARAMETER);
                    return;
                }

                String end = getParameter(END_PARAMETER, request);
                if (end == null) {
                    missingRequiredParameter(response, END_PARAMETER);
                    return;
                }

                PhoneCallRecord call;
                try {
                    call = PhoneCallRecord.fromStrings(callerNumber, calleeNumber, begin, end);

                } catch (DateTimeParseException ex) {
                    response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid date/time format");
                    return;
                }

                this.phoneBills.computeIfAbsent(customer, ignored -> new ArrayList<>()).add(call);

        PrintWriter pw = response.getWriter();
                pw.println(Messages.addedPhoneCallForCustomer(customer));
        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Handles an HTTP DELETE request by removing all dictionary entries.  This
     * behavior is exposed for testing purposes only.  It's probably not
     * something that you'd want a real application to expose.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        this.phoneBills.clear();

        PrintWriter pw = response.getWriter();
        pw.println(Messages.allPhoneBillsDeleted());
        pw.flush();

        response.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        String message = Messages.missingRequiredParameter(parameterName);
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
    }

    /**
     * Writes the definition of the given word to the HTTP response.
     *
     * The text of the message is formatted with {@link TextDumper}
     */
    private void writeCalls(HttpServletResponse response, List<PhoneCallRecord> calls) throws IOException {
        calls.sort(Comparator.comparing(PhoneCallRecord::getBeginTime));
        PrintWriter pw = response.getWriter();
        TextDumper dumper = new TextDumper(pw);
        dumper.dump(calls);

        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }

    @VisibleForTesting
    List<PhoneCallRecord> getPhoneBill(String customer) {
        return this.phoneBills.getOrDefault(customer, List.of());
    }

}
