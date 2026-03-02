package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Project5 {

  public static final String MISSING_ARGS = "Missing command line arguments";

  public static void main(String... args) {
    String hostName = null;
    String portString = null;
    boolean search = false;
    boolean print = false;

    List<String> positional = new ArrayList<>();

    for (int index = 0; index < args.length; index++) {
      String arg = args[index];
      switch (arg) {
        case "-README":
          printReadme();
          return;
        case "-search":
          search = true;
          break;
        case "-print":
          print = true;
          break;
        case "-host":
          if (index + 1 >= args.length) {
            usage("Missing host name");
            return;
          }
          hostName = args[++index];
          break;
        case "-port":
          if (index + 1 >= args.length) {
            usage("Missing port");
            return;
          }
          portString = args[++index];
          break;
        default:
          positional.add(arg);
      }
    }

    if ((hostName == null) != (portString == null)) {
      usage("Host and port must be specified together");
      return;
    }

    if (hostName == null) {
      usage(MISSING_ARGS);
      return;
    }

    int port;
    try {
      port = Integer.parseInt(portString);

    } catch (NumberFormatException ex) {
      usage("Port \"" + portString + "\" must be an integer");
      return;
    }

    PhoneBillRestClient client = new PhoneBillRestClient(hostName, port);

    try {
      if (search) {
        runSearch(client, positional);
      } else {
        runAdd(client, positional, print);
      }

    } catch (IOException | ParserException ex) {
      error("While contacting server: " + ex.getMessage());
    }
  }

  private static void runSearch(PhoneBillRestClient client, List<String> positional) throws IOException, ParserException {
    if (positional.size() != 1 && positional.size() != 7) {
      usage("Search requires customer or customer begin end");
      return;
    }

    String customer = positional.get(0);
    String begin = null;
    String end = null;

    if (positional.size() == 7) {
      begin = positional.get(1) + " " + positional.get(2) + " " + positional.get(3);
      end = positional.get(4) + " " + positional.get(5) + " " + positional.get(6);
    }

    List<PhoneCallRecord> calls = client.searchPhoneCalls(customer, begin, end);
    StringWriter sw = new StringWriter();
    new PrettyPrinter(sw).dump(customer, calls);
    System.out.println(sw);
  }

  private static void runAdd(PhoneBillRestClient client, List<String> positional, boolean print) throws IOException {
    if (positional.size() != 9) {
      usage("Missing command line arguments");
      return;
    }

    String customer = positional.get(0);
    String caller = positional.get(1);
    String callee = positional.get(2);
    String begin = positional.get(3) + " " + positional.get(4) + " " + positional.get(5);
    String end = positional.get(6) + " " + positional.get(7) + " " + positional.get(8);

    client.addPhoneCall(customer, caller, callee, begin, end);

    if (print) {
      System.out.println(caller + " -> " + callee + " from " + begin + " to " + end);
    }
  }

  private static void printReadme() {
    System.out.println("Project 5: REST-ful Phone Bill Web Service client");
  }

  private static void error(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("usage: java -jar target/phonebill-client.jar [options] <args>");
    err.println("options: -host <hostname> -port <port> -search -print -README");
  }
}
