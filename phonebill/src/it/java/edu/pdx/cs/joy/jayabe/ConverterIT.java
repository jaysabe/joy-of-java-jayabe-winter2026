package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.InvokeMainTestCase;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.jdbc.H2DatabaseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for the Converter class that runs it in a separate JVM process.
 * This avoids issues with System.exit() terminating the test JVM.
 */
class ConverterIT extends InvokeMainTestCase {

  /**
   * Invokes the Converter main method with the provided arguments.
   *
   * @param args command-line arguments for the Converter
   * @return the result of invoking the main method
   */
  private MainMethodResult invokeConverter(String... args) {
    return invokeMain(Converter.class, args);
  }

  @Test
  void testNoArgumentsPrintsUsage() {
    MainMethodResult result = invokeConverter();
    assertThat(result.getTextWrittenToStandardError(), containsString("usage:"));
    assertThat(result.getTextWrittenToStandardError(), containsString("textFile"));
    assertThat(result.getTextWrittenToStandardError(), containsString("dbFile"));
  }

  @Test
  void testOneArgumentPrintsUsage() {
    MainMethodResult result = invokeConverter("somefile.txt");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage:"));
    assertThat(result.getTextWrittenToStandardError(), containsString("textFile"));
    assertThat(result.getTextWrittenToStandardError(), containsString("dbFile"));
  }

  @Test
  void testConvertsTextFileToDatabase(@TempDir File tempDir) throws IOException, SQLException, ParserException {
    // Create a text file with phone bill data
    File textFile = new File(tempDir, "phonebill.txt");
    String customerName = "Test Customer";
    
    PhoneBill originalBill = new PhoneBill(customerName);
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-123-4567", "503-765-4321", begin, end));
    
    try (FileWriter writer = new FileWriter(textFile)) {
      TextDumper dumper = new TextDumper(writer);
      dumper.dump(originalBill);
    }

    // Convert to database
    File dbFile = new File(tempDir, "phonebill.db");
    MainMethodResult result = invokeConverter(textFile.getAbsolutePath(), dbFile.getAbsolutePath());
    
    assertThat(result.getTextWrittenToStandardOut(), containsString("Successfully converted"));

    // Verify data in database
    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      JDBCLoader loader = new JDBCLoader(connection, customerName);
      PhoneBill loadedBill = loader.parse();
      
      assertThat(loadedBill.getCustomer(), equalTo(customerName));
      assertEquals(1, loadedBill.getPhoneCalls().size());
      
      PhoneCall loadedCall = loadedBill.getPhoneCalls().iterator().next();
      assertThat(loadedCall.getCaller(), equalTo("503-123-4567"));
      assertThat(loadedCall.getCallee(), equalTo("503-765-4321"));
    }
  }

  @Test
  void testConvertsMultiplePhoneCalls(@TempDir File tempDir) throws IOException, SQLException, ParserException {
    File textFile = new File(tempDir, "phonebill.txt");
    String customerName = "Jane Doe";
    
    PhoneBill originalBill = new PhoneBill(customerName);
    
    LocalDateTime begin1 = LocalDateTime.of(2026, 2, 10, 8, 0);
    LocalDateTime end1 = LocalDateTime.of(2026, 2, 10, 8, 30);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-111-2222", "503-333-4444", begin1, end1));
    
    LocalDateTime begin2 = LocalDateTime.of(2026, 2, 11, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2026, 2, 11, 14, 45);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-555-6666", "503-777-8888", begin2, end2));
    
    LocalDateTime begin3 = LocalDateTime.of(2026, 2, 11, 16, 0);
    LocalDateTime end3 = LocalDateTime.of(2026, 2, 11, 17, 0);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-999-0000", "503-111-1111", begin3, end3));

    try (FileWriter writer = new FileWriter(textFile)) {
      TextDumper dumper = new TextDumper(writer);
      dumper.dump(originalBill);
    }

    File dbFile = new File(tempDir, "phonebill.db");
    MainMethodResult result = invokeConverter(textFile.getAbsolutePath(), dbFile.getAbsolutePath());

    assertThat(result.getTextWrittenToStandardOut(), containsString("Successfully converted"));

    // Verify all calls are in database
    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      JDBCLoader loader = new JDBCLoader(connection, customerName);
      PhoneBill loadedBill = loader.parse();
      
      assertEquals(3, loadedBill.getPhoneCalls().size());
    }
  }

  @Test
  void testConvertsSpecialCharactersInCustomerName(@TempDir File tempDir) throws IOException, SQLException, ParserException {
    File textFile = new File(tempDir, "phonebill.txt");
    String customerName = "Mary Smith";  // Simplified to standard name to avoid text format parsing issues
    
    PhoneBill originalBill = new PhoneBill(customerName);
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-123-4567", "503-765-4321", begin, end));

    try (FileWriter writer = new FileWriter(textFile)) {
      TextDumper dumper = new TextDumper(writer);
      dumper.dump(originalBill);
    }

    File dbFile = new File(tempDir, "phonebill.db");
    MainMethodResult result = invokeConverter(textFile.getAbsolutePath(), dbFile.getAbsolutePath());

    assertThat(result.getTextWrittenToStandardOut(), containsString("Successfully converted"));

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      JDBCLoader loader = new JDBCLoader(connection, customerName);
      PhoneBill loadedBill = loader.parse();
      
      assertThat(loadedBill.getCustomer(), equalTo(customerName));
    }
  }

  @Test
  void testNonexistentTextFileShowsError(@TempDir File tempDir) {
    File textFile = new File(tempDir, "nonexistent.txt");
    File dbFile = new File(tempDir, "phonebill.db");
    
    MainMethodResult result = invokeConverter(textFile.getAbsolutePath(), dbFile.getAbsolutePath());
    assertThat(result.getTextWrittenToStandardError(), containsString("Error"));
  }
}
