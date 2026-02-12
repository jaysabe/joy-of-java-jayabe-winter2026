package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.PhoneBillParser;

import java.sql.*;

/**
 * A {@link PhoneBillParser} that loads phone bills from an H2 relational database.
 * This class queries the database to retrieve customer and phone call information
 * and constructs a {@link PhoneBill} object from the results.
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
public class JDBCLoader implements PhoneBillParser<PhoneBill> {
  private final Connection connection;
  private final String customerName;

  /**
   * Creates a new JDBCLoader with the specified database connection and customer name.
   *
   * @param connection the database connection to use for loading phone bills
   * @param customerName the name of the customer whose phone bill to load
   */
  public JDBCLoader(Connection connection, String customerName) {
    this.connection = connection;
    this.customerName = customerName;
  }

  /**
   * Parses and loads a phone bill from the database for the specified customer.
   * If the customer exists in the database, retrieves all their phone calls and
   * constructs a {@link PhoneBill} object. If the customer doesn't exist,
   * returns an empty phone bill for that customer.
   *
   * @return the phone bill loaded from the database
   * @throws ParserException if a database error occurs during loading
   */
  @Override
  public PhoneBill parse() throws ParserException {
    try {
      PhoneBill bill = new PhoneBill(customerName);

      // Get customer ID
      Integer customerId = getCustomerId(customerName);
      
      if (customerId == null) {
        // Customer doesn't exist in database, return empty bill
        return bill;
      }

      // Load all phone calls for this customer
      loadPhoneCalls(customerId, bill);

      return bill;
    } catch (SQLException e) {
      throw new ParserException("Error loading phone bill from database: " + e.getMessage(), e);
    }
  }

  /**
   * Gets the customer ID for the given customer name.
   *
   * @param customerName the name of the customer
   * @return the customer ID, or null if the customer doesn't exist
   * @throws SQLException if a database error occurs
   */
  private Integer getCustomerId(String customerName) throws SQLException {
    String selectSQL = "SELECT id FROM customers WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      stmt.setString(1, customerName);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("id");
        }
      }
    }
    return null;
  }

  /**
   * Loads all phone calls for the specified customer and adds them to the phone bill.
   *
   * @param customerId the ID of the customer
   * @param bill the phone bill to add calls to
   * @throws SQLException if a database error occurs
   */
  private void loadPhoneCalls(int customerId, PhoneBill bill) throws SQLException {
    String selectSQL = "SELECT caller, callee, begin, \"end\" FROM phone_calls WHERE customer_id = ?";
    
    try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
      stmt.setInt(1, customerId);
      
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          String caller = rs.getString("caller");
          String callee = rs.getString("callee");
          Timestamp beginTimestamp = rs.getTimestamp("begin");
          Timestamp endTimestamp = rs.getTimestamp("end");

          PhoneCall call = new PhoneCall(
            bill.getCustomer(),
            caller,
            callee,
            beginTimestamp.toLocalDateTime(),
            endTimestamp.toLocalDateTime()
          );
          
          bill.addPhoneCall(call);
        }
      }
    }
  }
}
