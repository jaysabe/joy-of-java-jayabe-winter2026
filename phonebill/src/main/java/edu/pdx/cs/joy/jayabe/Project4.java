package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.jdbc.H2DatabaseHelper;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The <code>Project4</code> class is the main entry point for the Phone Bill application.
 * This application creates a phone bill and phone call record based on command-line arguments.
 * It validates input data including phone numbers, dates, and times, and supports optional
 * flags for printing call details, pretty printing, and displaying a README.
 *
 * <p>The program accepts the following command-line arguments:
 * <ul>
 *   <li>Customer name (can be quoted if it contains spaces)</li>
 *   <li>Caller's phone number (format: nnn-nnn-nnnn)</li>
 *   <li>Callee's phone number (format: nnn-nnn-nnnn)</li>
 *   <li>Begin date and time (format: mm/dd/yyyy h:mm am/pm) - three arguments</li>
 *   <li>End date and time (format: mm/dd/yyyy h:mm am/pm) - three arguments</li>
 * </ul>
 *
 * <p>Supported options:
 * <ul>
 *   <li><code>-print</code>: Prints a description of the phone call</li>
 *   <li><code>-README</code>: Displays README information and exits</li>
 *   <li><code>-textFile file</code>: Where to read/write the phone bill</li>
 *   <li><code>-dbFile file</code>: Location of relational database file</li>
 *   <li><code>-pretty file</code>: Pretty print the phone bill to a text file or standard out (file -)</li>
 * </ul>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class Project4 {
  private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
  private static final String[] errors = {
          "Missing customer information",
          "Missing caller phone number",
          "Missing callee phone number",
          "Missing begin date",
          "Missing begin time",
          "Missing begin am/pm",
          "Missing end date",
          "Missing end time",
          "Missing end am/pm"
  };

  /**
   * Creates a new instance of the {@code Project4} class.
   * This constructor is used to initialize the application logic
   * for parsing and running the phone bill program.
   */
  Project4() {
    //empty constructor
  }

  /**
   * Date and time formatter for parsing date-time strings in the format MM/dd/yyyy HH:mm.
   * Used to convert string representations of dates and times into LocalDateTime objects for file I/O.
   */
  static final DateTimeFormatter FILE_DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

  /**
   * Date and time formatter for parsing command-line date-time strings with AM/PM.
   * Format: MM/dd/yyyy h:mm a (e.g., "01/02/2026 9:16 PM")
   */
  static final DateTimeFormatter CLI_DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

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
      new Project4().parseAndRun(args);
    } catch (IllegalArgumentException ex) {
      System.err.println("Error: " + ex.getMessage());
      System.err.println();
      printUsage();
    }
  }

  /**
   * Parses the command-line arguments and executes the appropriate program logic.
   * This method handles option flags (-print, -README, -textFile, -dbFile, -pretty), extracts and validates
   * customer information and call details, creates a phone call and phone bill,
   * and optionally prints or pretty prints the bill.
   *
   * <p>The method processes arguments in the following order:
   * <ol>
   *   <li>Processes option flags (must appear before other arguments)</li>
   *   <li>Extracts customer name (handles quoted names with spaces)</li>
   *   <li>Extracts and validates phone numbers, dates, and times</li>
   *   <li>Creates PhoneCall and PhoneBill objects</li>
   *   <li>Optionally prints call details if -print flag is set</li>
   *   <li>Optionally pretty prints the bill if -pretty flag is set</li>
   * </ol>
   *
   * @param args the command-line arguments to parse
   * @throws IllegalArgumentException if an unknown option is provided or validation fails
   */
  void parseAndRun(String... args) {
    boolean print = false;
    boolean readme = false;
    boolean textFileFlag = false;
    boolean dbFileFlag = false;
    boolean prettyFlag = false;
    String textFileName = null;
    String dbFileName = null;
    String prettyFileName = null;
    List<String> arguments = new ArrayList<>(9);

    if (args.length == 0) {
      System.err.println(errors[0]);
      printUsage();
      return;
    }

    // First pass: process all options
    int i = 0;
    while (i < args.length && args[i].startsWith("-")) {
      String arg = args[i];
      
      switch (arg) {
          case "-print" -> print = true;
          case "-README" -> readme = true;
          case "-textFile" -> {
            if (++i < args.length) {
              textFileName = args[i];
              textFileFlag = true;
            } else {
              throw new IllegalArgumentException("-textFile requires a file path argument");
            }
          }
          case "-dbFile" -> {
            if (++i < args.length) {
              dbFileName = args[i];
              dbFileFlag = true;
            } else {
              throw new IllegalArgumentException("-dbFile requires a file path argument");
            }
          }
          case "-pretty" -> {
            if (++i < args.length) {
              prettyFileName = args[i];
              prettyFlag = true;
            } else {
              throw new IllegalArgumentException("-pretty requires a file path argument");
            }
          }
          default -> throw new IllegalArgumentException("Unknown option: " + arg);
      }
      i++;
    }

    // Validate that both -textFile and -dbFile are not specified
    if (textFileFlag && dbFileFlag) {
      throw new IllegalArgumentException("Cannot specify both -textFile and -dbFile");
    }

    if (readme) {
      printREADME();
      return;
    }

    // Second pass: collect remaining arguments as customer data
    while (i < args.length) {
      String arg = args[i];
      
      // Handle quoted customer name
      if (arg.startsWith("\"")) {
        StringBuilder sb = new StringBuilder();
        sb.append(arg.substring(1));
        while (!arg.endsWith("\"") && i + 1 < args.length) {
          i++;
          arg = args[i];
          sb.append(" ").append(arg);
        }
        String customer = sb.toString();
        if (customer.endsWith("\"")) {
          customer = customer.substring(0, customer.length() - 1);
        }
        arguments.add(customer);
      } else {
        arguments.add(arg);
      }
      i++;
    }

    int argCount = arguments.size();
    if (argCount < 9) {
      System.err.println(errors[Math.min(argCount, errors.length - 1)]);
      printUsage();
      return;
    } else if (argCount > 9) {
      System.err.println("Too many command line arguments");
      printUsage();
      return;
    }

    String customer = arguments.get(0);
    String callerNumber = arguments.get(1);
    String calleeNumber = arguments.get(2);
    String beginDate = arguments.get(3);
    String beginTime = arguments.get(4);
    String beginAmPm = arguments.get(5);
    String endDate = arguments.get(6);
    String endTime = arguments.get(7);
    String endAmPm = arguments.get(8);
    
    // Combine date, time, and AM/PM
    String beginDateTime = beginDate + " " + beginTime + " " + beginAmPm;
    String endDateTime = endDate + " " + endTime + " " + endAmPm;

    validatePhoneNumber(callerNumber, "caller number");
    validatePhoneNumber(calleeNumber, "callee number");
    validateDateTime(beginDateTime, "begin");
    validateDateTime(endDateTime, "end");

    // Parse strings to LocalDateTime
    LocalDateTime begin = LocalDateTime.parse(beginDateTime, CLI_DATE_TIME_FORMATTER);
    LocalDateTime end = LocalDateTime.parse(endDateTime, CLI_DATE_TIME_FORMATTER);
    
    // Validate that end time is not before begin time
    if (end.isBefore(begin)) {
      throw new IllegalArgumentException("End time cannot be before begin time");
    }
    
    PhoneCall newCall = new PhoneCall(customer, callerNumber, calleeNumber, begin, end);
    PhoneBill bill = new PhoneBill(customer);

    if (textFileFlag) {
      try {
        bill = handleTextFile(textFileName, customer);
      } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
        return;
      }
    } else if (dbFileFlag) {
      try {
        bill = handleDatabaseFile(dbFileName, customer);
      } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
        return;
      }
    }
    
    bill.addPhoneCall(newCall);

    if (print) {
      System.out.println(newCall);
    }

    if (textFileFlag) {
      try {
        saveData(textFileName, bill);
      } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
      }
    } else if (dbFileFlag) {
      try {
        saveDatabaseData(dbFileName, bill);
      } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }

    if (prettyFlag) {
      try {
        prettyPrint(prettyFileName, bill);
      } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Siloed helper function to handle the creation or loading of a PhoneBill from a text file.
   * This manages file existence checks and customer name validation.
   * @param fileName The name of the file to load, or null if no file specified.
   * @param expectedCustomer The customer name provided via command line.
   * @return A PhoneBill object (either loaded from file or newly created).
   */
  PhoneBill handleTextFile(String fileName, String expectedCustomer) {
    if (fileName == null) {
      return new PhoneBill(expectedCustomer);
    }

    File file = new File(fileName);
    if (!file.exists()) {
      return new PhoneBill(expectedCustomer);
    }

    try (Reader reader = new FileReader(file)) {
      TextParser parser = new TextParser(reader);
      PhoneBill loadedBill = parser.parse();

      if (!loadedBill.getCustomer().equals(expectedCustomer)) {
        throw new IllegalArgumentException("Customer name in file (" + loadedBill.getCustomer()
                + ") does not match command line (" + expectedCustomer + ")");
      }
      return loadedBill;
    } catch (ParserException | IOException e) {
      throw new IllegalArgumentException("Error processing file: " + e.getMessage());
    }
  }

  /**
   * Siloed helper function to handle the creation or loading of a PhoneBill from a database.
   * This manages database connection and customer name validation.
   * @param dbFileName The name of the database file to load from.
   * @param expectedCustomer The customer name provided via command line.
   * @return A PhoneBill object (either loaded from database or newly created).
   */
  PhoneBill handleDatabaseFile(String dbFileName, String expectedCustomer) {
    if (dbFileName == null) {
      return new PhoneBill(expectedCustomer);
    }

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(new File(dbFileName))) {
      PhoneBillDAO.createTable(connection);
      
      JDBCLoader loader = new JDBCLoader(connection, expectedCustomer);
      PhoneBill loadedBill = loader.parse();

      if (!loadedBill.getCustomer().equals(expectedCustomer)) {
        throw new IllegalArgumentException("Customer name in database (" + loadedBill.getCustomer()
                + ") does not match command line (" + expectedCustomer + ")");
      }
      return loadedBill;
    } catch (ParserException e) {
      throw new IllegalArgumentException("Error parsing database: " + e.getMessage());
    } catch (SQLException e) {
      throw new IllegalArgumentException("Error accessing database: " + e.getMessage());
    }
  }

  /**
   * Siloed helper function to handle the persistence of PhoneBill data to a text file.
   * This manages the TextDumper and handles any I/O exceptions.
   * @param fileName The name of the file to save to; if null, no data is saved.
   * @param bill The PhoneBill object to be written to the file.
   */
  private void saveData(String fileName, PhoneBill bill) {
    if (fileName == null) {
      return;
    }

    try {
      File file = new File(fileName);
      File parentDir = file.getParentFile();
      if (parentDir != null && !parentDir.exists()) {
        parentDir.mkdirs();
      }
      
      try (Writer writer = new FileWriter(file)) {
        TextDumper dumper = new TextDumper(writer);
        dumper.dump(bill);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Error writing to file: " + e.getMessage());
    }
  }

  /**
   * Siloed helper function to handle the persistence of PhoneBill data to a database.
   * This manages the JDBCDumper and handles any database exceptions.
   * @param dbFileName The name of the database file to save to; if null, no data is saved.
   * @param bill The PhoneBill object to be written to the database.
   */
  private void saveDatabaseData(String dbFileName, PhoneBill bill) {
    if (dbFileName == null) {
      return;
    }

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(new File(dbFileName))) {
      PhoneBillDAO.createTable(connection);
      
      JDBCDumper dumper = new JDBCDumper(connection);
      dumper.dump(bill);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error writing to database: " + e.getMessage());
    } catch (SQLException e) {
      throw new IllegalArgumentException("Error accessing database: " + e.getMessage());
    }
  }

  /**
   * Pretty prints the phone bill to a file or standard output.
   * @param fileName The name of the file to pretty print to, or "-" for standard output.
   * @param bill The PhoneBill object to be pretty printed.
   */
  private void prettyPrint(String fileName, PhoneBill bill) {
    if (fileName == null) {
      return;
    }

    try {
      Writer writer;
      if ("-".equals(fileName)) {
        writer = new OutputStreamWriter(System.out);
      } else {
        File file = new File(fileName);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
          parentDir.mkdirs();
        }
        writer = new FileWriter(file);
      }
      
      try (writer) {
        PrettyPrinter printer = new PrettyPrinter(writer);
        printer.dump(bill);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Error pretty printing: " + e.getMessage());
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
   * Validates that a date-time string matches the expected format (mm/dd/yyyy h:mm am/pm).
   *
   * @param dateTime the date-time string to validate
   * @param field the descriptive name of the field being validated (e.g., "begin")
   * @throws IllegalArgumentException if the date-time does not match the expected format
   */
  private static void validateDateTime(String dateTime, String field) {
    try {
      LocalDateTime.parse(dateTime, CLI_DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(
              "Invalid " + field + " date/time format: " + dateTime + " (expected mm/dd/yyyy h:mm am/pm)"
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
    System.out.println("  args are (in this order):");
    System.out.println("    customer         Person whose phone bill we're modeling");
    System.out.println("    callerNumber     Phone number of caller");
    System.out.println("    calleeNumber     Phone number of person who was called");
    System.out.println("    begin            Call begin date/time AM/PM");
    System.out.println("    end              Call end date/time AM/PM");
    System.out.println("  options are (options may appear in any order):");
    System.out.println("    -dbFile file     Location of relational database file");
    System.out.println("    -textFile file   Where to read/write the phone bill");
    System.out.println("    -pretty file     Pretty print the phone bill to a text file");
    System.out.println("                     or standard out (file -)");
    System.out.println("    -print           Prints a description of the new phone call");
    System.out.println("    -README          Prints a README for this project and exits");
    System.out.println();
    System.out.println("  It is an error to specify both -textFile and -dbFile.");
  }

  /**
   * Prints README information about the Phone Bill application to standard output.
   * This includes a brief description of the project's purpose and functionality.
   */
  private static void printREADME() {
    System.out.println("Project 4: Storing A Phone Bill in a Relational Database");
    System.out.println();
    System.out.println("This program creates a phone bill and a phone call");
    System.out.println("using information provided on the command line.");
    System.out.println();
    System.out.println("Phone calls are sorted chronologically by their begin time.");
    System.out.println("If two phone calls begin at the same time, they are sorted by");
    System.out.println("the caller's phone number.");
    System.out.println();
    System.out.println("The program supports reading/writing phone bills to text files");
    System.out.println("or to an H2 relational database. Pretty printing is also supported");
    System.out.println("to display phone bills with call duration in minutes.");
    System.out.println();
    System.out.println("Date and time format: mm/dd/yyyy h:mm am/pm");
    System.out.println("Example: 01/02/2026 9:16 PM");
    System.out.println();
    System.out.println("Author: Jay Abegglen");
  }
}
