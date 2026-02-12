package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.jdbc.H2DatabaseHelper;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The <code>Converter</code> class converts a phone bill representation from a text file
 * to a relational database format. This class reads phone bill data from a text file
 * using {@link TextParser} and writes it to an H2 database using {@link JDBCDumper}.
 *
 * <p>Command-line usage:
 * <pre>
 *   java -cp target/phonebill-1.0.0.jar edu.pdx.cs.joy.&lt;login-id&gt;.Converter textFile dbFile
 * </pre>
 *
 * <p>Arguments:
 * <ul>
 *   <li><code>textFile</code> - The name of the text file to convert</li>
 *   <li><code>dbFile</code> - The name of the file containing the database</li>
 * </ul>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class Converter {

  /**
   * Main entry point for the Converter application.
   * Converts a phone bill from a text file to a database file.
   *
   * @param args command-line arguments: [textFile, dbFile]
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Error: Missing command line arguments");
      printUsage();
      return;
    }

    String textFile = args[0];
    String dbFile = args[1];

    try {
      convertTextFileToDatabase(textFile, dbFile);
      System.out.println("Successfully converted " + textFile + " to " + dbFile);
    } catch (IOException e) {
      System.err.println("Error reading text file: " + e.getMessage());
    } catch (ParserException e) {
      System.err.println("Error parsing text file: " + e.getMessage());
    } catch (SQLException e) {
      System.err.println("Error accessing database: " + e.getMessage());
    }
  }

  /**
   * Converts a phone bill from a text file to a database.
   *
   * @param textFile the path to the text file containing the phone bill
   * @param dbFile the path to the database file
   * @throws IOException if an I/O error occurs
   * @throws ParserException if the text file cannot be parsed
   * @throws SQLException if a database error occurs
   */
  private static void convertTextFileToDatabase(String textFile, String dbFile)
          throws IOException, ParserException, SQLException {
    
    // Parse the phone bill from the text file
    PhoneBill bill;
    try (FileReader reader = new FileReader(textFile)) {
      TextParser parser = new TextParser(reader);
      bill = parser.parse();
    }

    // Save the phone bill to the database
    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(new File(dbFile))) {
      PhoneBillDAO.createTable(connection);
      
      JDBCDumper dumper = new JDBCDumper(connection);
      dumper.dump(bill);
    }
  }

  /**
   * Prints usage information to standard error.
   */
  private static void printUsage() {
    System.err.println("usage: java -cp target/phonebill-1.0.0.jar edu.pdx.cs.joy.<login-id>.Converter textFile dbFile");
    System.err.println("  args are (in this order):");
    System.err.println("    textFile  The name of the text file to convert");
    System.err.println("    dbFile    The name of the file containing the database");
  }
}
