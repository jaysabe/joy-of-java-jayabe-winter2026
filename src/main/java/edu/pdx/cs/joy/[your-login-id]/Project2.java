package edu.pdx.cs.joy.[your-login-id];

import edu.pdx.cs.joy.PhoneBill;
import edu.pdx.cs.joy.PhoneCall;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Project2 {

  public static void main(String[] args) {
    try {
      Arguments arguments = parseArguments(args);

      if (arguments.isReadme()) {
        printReadme();
        return;
      }

      File textFile = arguments.getTextFile();
      PhoneBill bill = null;

      // Read existing phone bill if file exists
      if (textFile != null && textFile.exists()) {
        try {
          TextParser parser = new TextParser(new FileReader(textFile));
          bill = parser.parse();
          
          // Verify customer name matches
          if (!bill.getCustomer().equals(arguments.getCustomer())) {
            System.err.println("Error: Customer name '" + arguments.getCustomer() + 
                             "' does not match existing bill customer '" + 
                             bill.getCustomer() + "'");
            System.exit(1);
          }
        } catch (ParserException e) {
          System.err.println("Error parsing file: " + e.getMessage());
          System.exit(1);
        }
      }

      // Create new bill if one doesn't exist
      if (bill == null) {
        bill = new PhoneBillImpl(arguments.getCustomer());
      }

      // Add the new phone call
      PhoneCall call = new PhoneCall(
          arguments.getCallerNumber(),
          arguments.getCalleeNumber(),
          arguments.getBeginTime(),
          arguments.getEndTime()
      );
      bill.addPhoneCall(call);

      // Print call if requested
      if (arguments.isPrint()) {
        System.out.println(call);
      }

      // Write to file if specified
      if (textFile != null) {
        ensureParentDirectoriesExist(textFile);
        try (Writer writer = new FileWriter(textFile)) {
          TextDumper dumper = new TextDumper(writer);
          dumper.dump(bill);
        }
      }

    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }

  private static Arguments parseArguments(String[] args) throws IllegalArgumentException {
    Arguments arguments = new Arguments();
    
    int i = 0;
    while (i < args.length && args[i].startsWith("-")) {
      if ("-textFile".equals(args[i])) {
        if (i + 1 >= args.length) {
          throw new IllegalArgumentException("-textFile requires a file path argument");
        }
        arguments.setTextFile(new File(args[++i]));
      } else if ("-print".equals(args[i])) {
        arguments.setPrint(true);
      } else if ("-README".equals(args[i])) {
        arguments.setReadme(true);
        return arguments;
      } else {
        throw new IllegalArgumentException("Unknown option: " + args[i]);
      }
      i++;
    }

    // Parse required arguments
    if (i + 4 > args.length) {
      throw new IllegalArgumentException(
          "Missing required arguments. Usage: java -jar target/phonebill-1.0.0.jar " +
          "[options] <customer> <callerNumber> <calleeNumber> <begin> <end>");
    }

    arguments.setCustomer(args[i++]);
    arguments.setCallerNumber(args[i++]);
    arguments.setCalleeNumber(args[i++]);
    arguments.setBeginTime(args[i++]);
    arguments.setEndTime(args[i++]);

    if (i < args.length) {
      throw new IllegalArgumentException("Extraneous command line arguments: " + args[i]);
    }

    return arguments;
  }

  private static void printReadme() {
    System.out.println("Project 2: Storing Your Phone Bill in a Text File");
    System.out.println("====================================================");
    System.out.println();
    System.out.println("Usage: java -jar target/phonebill-1.0.0.jar [options] <args>");
    System.out.println();
    System.out.println("Arguments:");
    System.out.println("  customer        Person whose phone bill we're modeling");
    System.out.println("  callerNumber    Phone number of caller");
    System.out.println("  calleeNumber    Phone number of person who was called");
    System.out.println("  begin           Date and time call began (mm/dd/yyyy hh:mm)");
    System.out.println("  end             Date and time call ended (mm/dd/yyyy hh:mm)");
    System.out.println();
    System.out.println("Options:");
    System.out.println("  -textFile file  Where to read/write the phone bill");
    System.out.println("  -print          Prints a description of the new phone call");
    System.out.println("  -README         Prints this message and exits");
  }

  private static void ensureParentDirectoriesExist(File file) throws IOException {
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      Files.createDirectories(parentDir.toPath());
    }
  }

  private static class Arguments {
    private String customer;
    private String callerNumber;
    private String calleeNumber;
    private String beginTime;
    private String endTime;
    private File textFile;
    private boolean print;
    private boolean readme;

    // ...getters and setters...
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getCallerNumber() { return callerNumber; }
    public void setCallerNumber(String callerNumber) { this.callerNumber = callerNumber; }

    public String getCalleeNumber() { return calleeNumber; }
    public void setCalleeNumber(String calleeNumber) { this.calleeNumber = calleeNumber; }

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public File getTextFile() { return textFile; }
    public void setTextFile(File textFile) { this.textFile = textFile; }

    public boolean isPrint() { return print; }
    public void setPrint(boolean print) { this.print = print; }

    public boolean isReadme() { return readme; }
    public void setReadme(boolean readme) { this.readme = readme; }
  }
}
