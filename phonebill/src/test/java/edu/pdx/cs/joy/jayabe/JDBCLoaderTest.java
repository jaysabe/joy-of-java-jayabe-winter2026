package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JDBCLoader class.
 * Tests loading phone bills from an H2 in-memory database.
 */
public class JDBCLoaderTest {

  private Connection connection;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = DriverManager.getConnection("jdbc:h2:mem:jdbcloader_test");

    // Create the tables
    PhoneBillDAO.createTable(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @Test
  public void canLoadEmptyPhoneBillForNonExistentCustomer() throws ParserException {
    String customerName = "NonExistent Customer";
    JDBCLoader loader = new JDBCLoader(connection, customerName);

    PhoneBill bill = loader.parse();

    assertThat(bill, is(notNullValue()));
    assertThat(bill.getCustomer(), equalTo(customerName));
    assertThat(bill.getPhoneCalls().size(), equalTo(0));
  }

  @Test
  public void canLoadEmptyPhoneBillForExistingCustomerWithNoCalls() throws ParserException, SQLException {
    String customerName = "John Doe";
    
    // Insert customer without any phone calls
    String insertSQL = "INSERT INTO customers (name) VALUES (?)";
    try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
      stmt.setString(1, customerName);
      stmt.executeUpdate();
    }

    JDBCLoader loader = new JDBCLoader(connection, customerName);
    PhoneBill bill = loader.parse();

    assertThat(bill, is(notNullValue()));
    assertThat(bill.getCustomer(), equalTo(customerName));
    assertThat(bill.getPhoneCalls().size(), equalTo(0));
  }

  @Test
  public void canLoadPhoneBillWithOneCall() throws ParserException, SQLException, IOException {
    String customerName = "Jane Smith";
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);

    // Use JDBCDumper to set up test data
    PhoneBill originalBill = new PhoneBill(customerName);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-123-4567", "503-765-4321", begin, end));
    
    JDBCDumper dumper = new JDBCDumper(connection);
    dumper.dump(originalBill);

    // Load the phone bill
    JDBCLoader loader = new JDBCLoader(connection, customerName);
    PhoneBill loadedBill = loader.parse();

    assertThat(loadedBill.getCustomer(), equalTo(customerName));
    assertThat(loadedBill.getPhoneCalls().size(), equalTo(1));
    
    PhoneCall loadedCall = loadedBill.getPhoneCalls().iterator().next();
    assertThat(loadedCall.getCaller(), equalTo("503-123-4567"));
    assertThat(loadedCall.getCallee(), equalTo("503-765-4321"));
    assertThat(loadedCall.getBeginTime(), equalTo(begin));
    assertThat(loadedCall.getEndTime(), equalTo(end));
  }

  @Test
  public void canLoadPhoneBillWithMultipleCalls() throws ParserException, SQLException, IOException {
    String customerName = "Bob Johnson";
    
    // Use JDBCDumper to set up test data
    PhoneBill originalBill = new PhoneBill(customerName);
    
    LocalDateTime begin1 = LocalDateTime.of(2026, 2, 11, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2026, 2, 11, 9, 15);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-111-2222", "503-333-4444", begin1, end1));
    
    LocalDateTime begin2 = LocalDateTime.of(2026, 2, 11, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2026, 2, 11, 14, 45);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-555-6666", "503-777-8888", begin2, end2));
    
    LocalDateTime begin3 = LocalDateTime.of(2026, 2, 11, 16, 30);
    LocalDateTime end3 = LocalDateTime.of(2026, 2, 11, 17, 0);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-999-0000", "503-111-1111", begin3, end3));

    JDBCDumper dumper = new JDBCDumper(connection);
    dumper.dump(originalBill);

    // Load the phone bill
    JDBCLoader loader = new JDBCLoader(connection, customerName);
    PhoneBill loadedBill = loader.parse();

    assertThat(loadedBill.getCustomer(), equalTo(customerName));
    assertThat(loadedBill.getPhoneCalls().size(), equalTo(3));
  }

  @Test
  public void loadsOnlyCallsForSpecifiedCustomer() throws ParserException, SQLException, IOException {
    String customer1 = "Customer One";
    String customer2 = "Customer Two";
    
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);

    // Create bills for two different customers
    PhoneBill bill1 = new PhoneBill(customer1);
    bill1.addPhoneCall(new PhoneCall(customer1, "503-111-1111", "503-222-2222", begin, end));
    bill1.addPhoneCall(new PhoneCall(customer1, "503-111-1111", "503-333-3333", begin, end));

    PhoneBill bill2 = new PhoneBill(customer2);
    bill2.addPhoneCall(new PhoneCall(customer2, "503-444-4444", "503-555-5555", begin, end));

    JDBCDumper dumper = new JDBCDumper(connection);
    dumper.dump(bill1);
    dumper.dump(bill2);

    // Load only customer1's phone bill
    JDBCLoader loader = new JDBCLoader(connection, customer1);
    PhoneBill loadedBill = loader.parse();

    assertThat(loadedBill.getCustomer(), equalTo(customer1));
    assertThat(loadedBill.getPhoneCalls().size(), equalTo(2));
    
    // Verify all calls belong to customer1
    for (PhoneCall call : loadedBill.getPhoneCalls()) {
      assertThat(call.getCaller(), startsWith("503-111"));
    }
  }

  @Test
  public void roundTripPreservesPhoneBillData() throws ParserException, SQLException, IOException {
    String customerName = "Alice Brown";
    
    PhoneBill originalBill = new PhoneBill(customerName);
    
    LocalDateTime begin1 = LocalDateTime.of(2026, 2, 10, 8, 30);
    LocalDateTime end1 = LocalDateTime.of(2026, 2, 10, 9, 15);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-234-5678", "503-876-5432", begin1, end1));
    
    LocalDateTime begin2 = LocalDateTime.of(2026, 2, 11, 13, 0);
    LocalDateTime end2 = LocalDateTime.of(2026, 2, 11, 13, 45);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-345-6789", "503-987-6543", begin2, end2));

    // Dump to database
    JDBCDumper dumper = new JDBCDumper(connection);
    dumper.dump(originalBill);

    // Load from database
    JDBCLoader loader = new JDBCLoader(connection, customerName);
    PhoneBill loadedBill = loader.parse();

    // Verify all data is preserved
    assertThat(loadedBill.getCustomer(), equalTo(originalBill.getCustomer()));
    assertThat(loadedBill.getPhoneCalls().size(), equalTo(originalBill.getPhoneCalls().size()));
    
    // Compare phone calls (they should be in same order after sorting)
    var originalCalls = originalBill.getPhoneCalls().iterator();
    var loadedCalls = loadedBill.getPhoneCalls().iterator();
    
    while (originalCalls.hasNext() && loadedCalls.hasNext()) {
      PhoneCall original = originalCalls.next();
      PhoneCall loaded = loadedCalls.next();
      
      assertThat(loaded.getCaller(), equalTo(original.getCaller()));
      assertThat(loaded.getCallee(), equalTo(original.getCallee()));
      assertThat(loaded.getBeginTime(), equalTo(original.getBeginTime()));
      assertThat(loaded.getEndTime(), equalTo(original.getEndTime()));
    }
  }

  @Test
  public void canHandleSpecialCharactersInCustomerName() throws ParserException, SQLException, IOException {
    String customerName = "O'Brien-Smith, Jr.";
    
    PhoneBill originalBill = new PhoneBill(customerName);
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);
    originalBill.addPhoneCall(new PhoneCall(customerName, "503-123-4567", "503-765-4321", begin, end));

    JDBCDumper dumper = new JDBCDumper(connection);
    dumper.dump(originalBill);

    JDBCLoader loader = new JDBCLoader(connection, customerName);
    PhoneBill loadedBill = loader.parse();

    assertThat(loadedBill.getCustomer(), equalTo(customerName));
    assertThat(loadedBill.getPhoneCalls().size(), equalTo(1));
  }
}
