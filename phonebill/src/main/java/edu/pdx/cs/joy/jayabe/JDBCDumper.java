package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.AbstractPhoneBill;
import edu.pdx.cs.joy.PhoneBillDumper;

import java.io.IOException;
import java.sql.*;

/**
 * A {@link PhoneBillDumper} that persists phone bills to an H2 relational database.
 * This class saves phone bill information into two tables: a customers table
 * and a phone_calls table, conforming to the entity-relationship diagram
 * specified for Project 4.
 *
 * <p>The database schema consists of:
 * <ul>
 *   <li>customers table: id (IDENTITY PK), name (VARCHAR(255))</li>
 *   <li>phone_calls table: id (IDENTITY PK), customer_id (FK), caller (CHAR(12)),
 *       callee (CHAR(12)), begin (TIMESTAMP), end (TIMESTAMP)</li>
 * </ul>
 *
 * @author Jay Abegglen
 * @version 1.0
 */
public class JDBCDumper implements PhoneBillDumper<AbstractPhoneBill<PhoneCall>> {
  private final Connection connection;

  /**
   * Creates a new JDBCDumper with the specified database connection.
   *
   * @param connection the database connection to use for persisting phone bills
   */
  public JDBCDumper(Connection connection) {
    this.connection = connection;
  }

  /**
   * Persists a phone bill to the database. This method first ensures the customer
   * exists in the customers table (inserting if necessary), then saves all phone calls
   * associated with the bill to the phone_calls table.
   *
   * @param bill the phone bill to persist
   * @throws IOException if a database error occurs during persistence
   */
  @Override
  public void dump(AbstractPhoneBill<PhoneCall> bill) throws IOException {
    try {
      // Get or create customer ID
      int customerId = getOrCreateCustomer(bill.getCustomer());

      // Save all phone calls for this customer
      for (PhoneCall call : bill.getPhoneCalls()) {
        savePhoneCall(customerId, call);
      }
    } catch (SQLException e) {
      throw new IOException("Error saving phone bill to database: " + e.getMessage(), e);
    }
  }

  /**
   * Gets the customer ID for the given customer name, or creates a new customer
   * if one doesn't exist.
   *
   * @param customerName the name of the customer
   * @return the customer ID
   * @throws SQLException if a database error occurs
   */
  private int getOrCreateCustomer(String customerName) throws SQLException {
    // First try to find existing customer
    String selectSQL = "SELECT id FROM customers WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      stmt.setString(1, customerName);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("id");
        }
      }
    }

    // Customer doesn't exist, create new one
    String insertSQL = "INSERT INTO customers (name) VALUES (?)";
    try (PreparedStatement stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, customerName);
      stmt.executeUpdate();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          throw new SQLException("Failed to get generated customer ID");
        }
      }
    }
  }

  /**
   * Saves a phone call to the database.
   *
   * @param customerId the ID of the customer making the call
   * @param call the phone call to save
   * @throws SQLException if a database error occurs
   */
  private void savePhoneCall(int customerId, PhoneCall call) throws SQLException {
    String insertSQL = "INSERT INTO phone_calls (customer_id, caller, callee, begin, \"end\") VALUES (?, ?, ?, ?, ?)";
    
    try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
      stmt.setInt(1, customerId);
      stmt.setString(2, call.getCaller());
      stmt.setString(3, call.getCallee());
      stmt.setTimestamp(4, Timestamp.valueOf(call.getBeginTime()));
      stmt.setTimestamp(5, Timestamp.valueOf(call.getEndTime()));
      stmt.executeUpdate();
    }
  }
}
