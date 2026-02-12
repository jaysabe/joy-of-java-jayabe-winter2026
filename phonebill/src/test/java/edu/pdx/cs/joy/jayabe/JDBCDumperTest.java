package edu.pdx.cs.joy.jayabe;

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
 * Unit tests for the JDBCDumper class.
 * Tests persisting phone bills to an H2 in-memory database.
 */
public class JDBCDumperTest {

  private Connection connection;
  private JDBCDumper dumper;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = DriverManager.getConnection("jdbc:h2:mem:jdbcdumper_test");

    // Create the tables
    PhoneBillDAO.createTable(connection);

    // Create the dumper
    dumper = new JDBCDumper(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @Test
  public void canPersistEmptyPhoneBill() throws IOException, SQLException {
    String customerName = "John Doe";
    PhoneBill bill = new PhoneBill(customerName);

    dumper.dump(bill);

    // Verify customer was created
    String selectSQL = "SELECT name FROM customers WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      stmt.setString(1, customerName);
      try (ResultSet rs = stmt.executeQuery()) {
        assertTrue(rs.next(), "Customer should exist in database");
        assertThat(rs.getString("name"), equalTo(customerName));
      }
    }
  }

  @Test
  public void canPersistPhoneBillWithOneCall() throws IOException, SQLException {
    String customerName = "Jane Smith";
    PhoneBill bill = new PhoneBill(customerName);
    
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);
    PhoneCall call = new PhoneCall(customerName, "503-123-4567", "503-765-4321", begin, end);
    bill.addPhoneCall(call);

    dumper.dump(bill);

    // Verify phone call was saved
    String selectSQL = "SELECT caller, callee, begin, \"end\" FROM phone_calls";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      try (ResultSet rs = stmt.executeQuery()) {
        assertTrue(rs.next(), "Phone call should exist in database");
        assertThat(rs.getString("caller"), equalTo("503-123-4567"));
        assertThat(rs.getString("callee"), equalTo("503-765-4321"));
        assertThat(rs.getTimestamp("begin").toLocalDateTime(), equalTo(begin));
        assertThat(rs.getTimestamp("end").toLocalDateTime(), equalTo(end));
      }
    }
  }

  @Test
  public void canPersistPhoneBillWithMultipleCalls() throws IOException, SQLException {
    String customerName = "Bob Johnson";
    PhoneBill bill = new PhoneBill(customerName);
    
    LocalDateTime begin1 = LocalDateTime.of(2026, 2, 11, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2026, 2, 11, 9, 15);
    bill.addPhoneCall(new PhoneCall(customerName, "503-111-2222", "503-333-4444", begin1, end1));
    
    LocalDateTime begin2 = LocalDateTime.of(2026, 2, 11, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2026, 2, 11, 14, 45);
    bill.addPhoneCall(new PhoneCall(customerName, "503-555-6666", "503-777-8888", begin2, end2));

    dumper.dump(bill);

    // Verify both phone calls were saved
    String selectSQL = "SELECT COUNT(*) as count FROM phone_calls";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      try (ResultSet rs = stmt.executeQuery()) {
        assertTrue(rs.next());
        assertThat(rs.getInt("count"), equalTo(2));
      }
    }
  }

  @Test
  public void customerNotDuplicatedWhenDumpingMultipleTimes() throws IOException, SQLException {
    String customerName = "Alice Brown";
    PhoneBill bill1 = new PhoneBill(customerName);
    PhoneBill bill2 = new PhoneBill(customerName);

    dumper.dump(bill1);
    dumper.dump(bill2);

    // Verify only one customer record exists
    String selectSQL = "SELECT COUNT(*) as count FROM customers WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      stmt.setString(1, customerName);
      try (ResultSet rs = stmt.executeQuery()) {
        assertTrue(rs.next());
        assertThat(rs.getInt("count"), equalTo(1));
      }
    }
  }

  @Test
  public void phoneCallsLinkedToCorrectCustomer() throws IOException, SQLException {
    String customer1 = "Customer One";
    String customer2 = "Customer Two";
    
    PhoneBill bill1 = new PhoneBill(customer1);
    LocalDateTime begin = LocalDateTime.of(2026, 2, 11, 10, 0);
    LocalDateTime end = LocalDateTime.of(2026, 2, 11, 10, 30);
    bill1.addPhoneCall(new PhoneCall(customer1, "503-111-1111", "503-222-2222", begin, end));

    PhoneBill bill2 = new PhoneBill(customer2);
    bill2.addPhoneCall(new PhoneCall(customer2, "503-333-3333", "503-444-4444", begin, end));

    dumper.dump(bill1);
    dumper.dump(bill2);

    // Verify phone calls are linked to correct customers
    String selectSQL = "SELECT c.name, pc.caller FROM customers c " +
                       "JOIN phone_calls pc ON c.id = pc.customer_id";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      try (ResultSet rs = stmt.executeQuery()) {
        int count = 0;
        while (rs.next()) {
          count++;
          String name = rs.getString("name");
          String caller = rs.getString("caller");
          
          if (name.equals(customer1)) {
            assertThat(caller, equalTo("503-111-1111"));
          } else if (name.equals(customer2)) {
            assertThat(caller, equalTo("503-333-3333"));
          }
        }
        assertThat(count, equalTo(2));
      }
    }
  }

  @Test
  public void canHandleSpecialCharactersInCustomerName() throws IOException, SQLException {
    String customerName = "O'Brien-Smith, Jr.";
    PhoneBill bill = new PhoneBill(customerName);
    
    assertDoesNotThrow(() -> dumper.dump(bill));

    // Verify customer was saved correctly
    String selectSQL = "SELECT name FROM customers WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      stmt.setString(1, customerName);
      try (ResultSet rs = stmt.executeQuery()) {
        assertTrue(rs.next());
        assertThat(rs.getString("name"), equalTo(customerName));
      }
    }
  }

  @Test
  public void nullPhoneBillDoesNotThrowException() {
    assertDoesNotThrow(() -> dumper.dump(null));
  }
}
