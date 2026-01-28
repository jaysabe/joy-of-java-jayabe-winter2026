package edu.pdx.cs.joy.jayabe;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Project1 {
  private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
  private static final Pattern TIME_PATTERN = Pattern.compile("\\d{1,2}:\\d{2}");
  private static final String[] errors = {
          "Missing customer name",
          "Missing caller phone number",
          "Missing callee phone number",
          "Missing begin date",
          "Missing begin time",
          "Missing end date",
          "Missing end time"
  };
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  public static void main(String[] args) {
    try {
      new Project1().parseAndRun(args);
    } catch (IllegalArgumentException ex) {
      System.err.println("Error: " + ex.getMessage());
      System.err.println();
      printUsage();
    }
  }

  void parseAndRun(String[] args) {
    boolean customerArg = false;
    boolean print = false;
    boolean readme = false;
    List<String> arguments = new ArrayList<>();

    if (args.length == 0) {
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
    if (argCount < errors.length) {
      System.err.println("Error: " + errors[argCount]);
      printUsage();
      return;
    } else if (argCount > errors.length) {
      System.err.println("Error: Too many command line arguments");
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


  private static void validatePhoneNumber(String number, String field) {
    if (!PHONE_PATTERN.matcher(number).matches()) {
      throw new IllegalArgumentException(
              "Invalid " + field + " format: " + number + " (expected nnn-nnn-nnnn)"
      );
    }
  }

  private static void validateTime(String time, String field) {
    if (!TIME_PATTERN.matcher(time).matches()) {
      throw new IllegalArgumentException(
              "Invalid " + field + " format: " + time + " (expected hh:mm)"
      );
    }
  }

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
