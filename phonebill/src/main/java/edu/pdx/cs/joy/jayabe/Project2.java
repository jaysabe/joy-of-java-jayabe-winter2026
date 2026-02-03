package edu.pdx.cs.joy.jayabe;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The <code>Project1</code> class is the main entry point for the Phone Bill application.
 * This application creates a phone bill and phone call record based on command-line arguments.
 * It validates input data including phone numbers, dates, and times, and supports optional
 * flags for printing call details and displaying a README.
 *
 * <p>The program accepts the following command-line arguments:
 * <ul>
 *   <li>Customer name (can be quoted if it contains spaces)</li>
 *   <li>Caller's phone number (format: nnn-nnn-nnnn)</li>
 *   <li>Callee's phone number (format: nnn-nnn-nnnn)</li>
 *   <li>Begin date (format: mm/dd/yyyy)</li>
 *   <li>Begin time (format: hh:mm)</li>
 *   <li>End date (format: mm/dd/yyyy)</li>
 *   <li>End time (format: hh:mm)</li>
 * </ul>
 *
 * <p>Supported options:
 * <ul>
 *   <li><code>-print</code>: Prints a description of the phone call</li>
 *   <li><code>-README</code>: Displays README information and exits</li>
 * </ul>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class Project2 {
  private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
  private static final Pattern TIME_PATTERN = Pattern.compile("\\d{1,2}:\\d{2}");
  private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
  private static final String[] errors = {
          "Missing customer information",
          "Missing caller phone number",
          "Missing callee phone number",
          "Missing begin date",
          "Missing begin time",
          "Missing end date",
          "Missing end time"
  };

  /**
   * Date and time formatter for parsing date-time strings in the format MM/dd/yyyy HH:mm.
   * Used to convert string representations of dates and times into LocalDateTime objects.
   */
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  /**
   * The main entry point for the Phone Bill application.
   * Parses command-line arguments and executes the program logic.
   * If an {@link IllegalArgumentException} is thrown during parsing or validation,
   * the error message is printed to standard error along with usage information.
   *
   * @param args command-line arguments provided by the user
   */
  public static void main(String... args) {
    try {
      new Project2().parseAndRun(args);
    } catch (IllegalArgumentException ex) {
      System.err.println("Error: " + ex.getMessage());
      System.err.println();
      printUsage();
    }
  }

  /**
   * Parses the command-line arguments and executes the appropriate program logic.
   * This method handles option flags (-print, -README), extracts and validates
   * customer information and call details, creates a phone call and phone bill,
   * and optionally prints the call information.
   *
   * <p>The method processes arguments in the following order:
   * <ol>
   *   <li>Processes option flags (must appear before other arguments)</li>
   *   <li>Extracts customer name (handles quoted names with spaces)</li>
   *   <li>Extracts and validates phone numbers, dates, and times</li>
   *   <li>Creates PhoneCall and PhoneBill objects</li>
   *   <li>Optionally prints call details if -print flag is set</li>
   * </ol>
   *
   * @param args the command-line arguments to parse
   * @throws IllegalArgumentException if an unknown option is provided or validation fails
   */
  void parseAndRun(String... args) {
    boolean customerArg = false;
    boolean print = false;
    boolean readme = false;
    List<String> arguments = new ArrayList<>(7);

    if (args.length == 0) {
      System.err.println(errors[0]);
      printUsage();
      return;
    }

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];

      if (!customerArg && arg.startsWith("-")) {
        if (arg.equals("-print")) print = true;
        else if (arg.equals("-README")) readme = true;
        else throw new IllegalArgumentException("Unknown option: " + arg);
      } else {
        customerArg = true;
        // Handle quoted customer
        if (arg.startsWith("\"")) {
          StringBuilder sb = new StringBuilder();
          sb.append(arg.substring(1)); // remove starting quote
          while (!arg.endsWith("\"") && i + 1 < args.length) {
            i++;
            arg = args[i];
            sb.append(" ").append(arg);
          }
          // remove trailing quote
          String customer = sb.toString();
          if (customer.endsWith("\"")) {
            customer = customer.substring(0, customer.length() - 1);
          }
          arguments.add(customer);
        } else {
          arguments.add(arg);
        }
      }
    }

    if (readme) {
      printREADME();
      return;
    }

    int argCount = arguments.size();
    if (argCount < 7) {
      System.err.println(errors[argCount]);
      printUsage();
      return;
    } else if (argCount > 7){
      System.err.println("Too many command line arguments");
      printUsage();
      return;
    }

    String customer = arguments.get(0);
    String callerNumber = arguments.get(1);
    String calleeNumber = arguments.get(2);
    String beginDate = arguments.get(3);
    String beginTime = arguments.get(4);
    String endDate = arguments.get(5);
    String endTime = arguments.get(6);

    validatePhoneNumber(callerNumber, "caller number");
    validatePhoneNumber(calleeNumber, "callee number");
    validateTime(beginTime, "begin time");
    validateTime(endTime, "end time");
    validateDate(beginDate, "begin date");
    validateDate(endDate, "end date");

    // Parse strings to LocalDateTime
    LocalDateTime begin = LocalDateTime.parse(beginDate + " " + beginTime, DATE_TIME_FORMATTER);
    LocalDateTime end = LocalDateTime.parse(endDate + " " + endTime, DATE_TIME_FORMATTER);

    PhoneCall call = new PhoneCall(customer, callerNumber, calleeNumber, begin, end);
    PhoneBill bill = new PhoneBill(customer);
    bill.addPhoneCall(call);

    if (print) {
      System.out.println(call);
    }


  }

  /**
   * Validates that a phone number matches the expected format (nnn-nnn-nnnn).
   *
   * @param number the phone number string to validate
   * @param field the descriptive name of the field being validated (e.g., "caller number")
   * @throws IllegalArgumentException if the phone number does not match the expected format
   */
  private static void validatePhoneNumber(String number, String field) {
    if (!PHONE_PATTERN.matcher(number).matches()) {
      throw new IllegalArgumentException(
              "Invalid " + field + " format: " + number + " (expected nnn-nnn-nnnn)"
      );
    }
  }

  /**
   * Validates that a time string matches the expected format (hh:mm or h:mm).
   *
   * @param time the time string to validate
   * @param field the descriptive name of the field being validated (e.g., "begin time")
   * @throws IllegalArgumentException if the time does not match the expected format
   */
  private static void validateTime(String time, String field) {
    if (!TIME_PATTERN.matcher(time).matches()) {
      throw new IllegalArgumentException(
              "Invalid " + field + " format: " + time + " (expected hh:mm)"
      );
    }
  }

  /**
   * Validates that a date string matches the expected format (dd-mm-yyyy).
   *
   * @param date the date string to validate
   * @param field the descriptive name of the field being validated (e.g., "begin date")
   * @throws IllegalArgumentException if the date does not match the expected format
   */
  private static void validateDate(String date, String field) {
    if (!DATE_PATTERN.matcher(date).matches()) {
      throw new IllegalArgumentException(
              "Invalid " + field + " format: " + date + " (expected dd-mm-yyyy)"
      );
    }
  }

  /**
   * Prints usage information for the Phone Bill application to standard output.
   * This includes the command syntax, descriptions of all required arguments,
   * and available option flags.
   */
  private static void printUsage() {
    System.out.println("usage: java -jar target/phonebill-1.0.0.jar [options] <args>");
    System.out.println("args are (in this order):");
    System.out.println("  customer       Person whose phone bill we're modeling");
    System.out.println("  callerNumber   Phone number of caller");
    System.out.println("  calleeNumber   Phone number of person who was called");
    System.out.println("  begin          Date and time call began (mm/dd/yyyy hh:mm)");
    System.out.println("  end            Date and time call ended (mm/dd/yyyy hh:mm)");
    System.out.println();
    System.out.println("options are:");
    System.out.println("  -print   Prints a description of the new phone call");
    System.out.println("  -README  Prints a README for this project and exits");
  }

  /**
   * Prints README information about the Phone Bill application to standard output.
   * This includes a brief description of the project's purpose and functionality.
   */
  private static void printREADME() {
    System.out.println("Project 1: Phone Bill Application");
    System.out.println();
    System.out.println("This program creates a phone bill and a phone call");
    System.out.println("using information provided on the command line.");
    System.out.println();
    System.out.println("No data is stored permanently. This project focuses");
    System.out.println("on command line parsing and error handling.");
  }
}
