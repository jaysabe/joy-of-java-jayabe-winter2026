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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Project4IT extends InvokeMainTestCase {

  private MainMethodResult invokeMain(String... args) {
    return invokeMain(Project4.class, args);
  }

  @Test
  void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getTextWrittenToStandardError().toLowerCase(),
      containsString("missing customer information"));
  }

  @Test
  void testFiveCommandLineArguments() {
    MainMethodResult result = invokeMain(
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00"
    );
    assertThat(result.getTextWrittenToStandardError().toLowerCase(),
      containsString("missing begin am/pm"));
  }

  @Test
  void testEightCommandLineArguments() {
    MainMethodResult result = invokeMain(
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00",
      "AM",
      "01/01/2026",
      "10:00"
    );
    assertThat(result.getTextWrittenToStandardError().toLowerCase(),
      containsString("missing end am/pm"));
  }

  @Test
  void testPrintOptionOutputsNewCall() {
    MainMethodResult result = invokeMain(
      "-print",
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00",
      "AM",
      "01/01/2026",
      "10:00",
      "AM"
    );
    assertThat(result.getTextWrittenToStandardOut(),
      containsString("Phone call from 123-456-7890 to 234-567-8901"));
  }

  @Test
  void testPrettyPrintToStandardOut() {
    MainMethodResult result = invokeMain(
      "-pretty",
      "-",
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00",
      "AM",
      "01/01/2026",
      "10:00",
      "AM"
    );
    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Alice"));
    assertThat(output, containsString("123-456-7890"));
    assertThat(output, containsString("234-567-8901"));
  }

  @Test
  void testEndTimeBeforeBeginTime() {
    MainMethodResult result = invokeMain(
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "10:00",
      "AM",
      "01/01/2026",
      "09:00",
      "AM"
    );
    assertThat(result.getTextWrittenToStandardError(),
      containsString("End time cannot be before begin time"));
  }

  @Test
  void testCustomerNameMismatchInTextFile(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "mismatch.txt");

    try (FileWriter writer = new FileWriter(file)) {
      writer.write("Alice");
    }

    MainMethodResult result = invokeMain(
      "-textFile",
      file.getAbsolutePath(),
      "Bob",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00",
      "AM",
      "01/01/2026",
      "10:00",
      "AM"
    );

    assertThat(result.getTextWrittenToStandardError(),
      containsString("does not match command line"));
  }

  @Test
  void testDbFilePersistsPhoneCall(@TempDir File tempDir)
    throws SQLException, ParserException {
    File dbFile = new File(tempDir, "phonebill.db");

    invokeMain(
      "-dbFile",
      dbFile.getAbsolutePath(),
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00",
      "AM",
      "01/01/2026",
      "10:00",
      "AM"
    );

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      JDBCLoader loader = new JDBCLoader(connection, "Alice");
      PhoneBill bill = loader.parse();
      assertEquals(1, bill.getPhoneCalls().size());
      PhoneCall call = bill.getPhoneCalls().iterator().next();
      assertEquals("123-456-7890", call.getCaller());
      assertEquals("234-567-8901", call.getCallee());
    }
  }

  @Test
  void testCannotSpecifyTextFileAndDbFileTogether() {
    MainMethodResult result = invokeMain(
      "-textFile",
      "somefile.txt",
      "-dbFile",
      "some.db",
      "Alice",
      "123-456-7890",
      "234-567-8901",
      "01/01/2026",
      "09:00",
      "AM",
      "01/01/2026",
      "10:00",
      "AM"
    );

    assertThat(result.getTextWrittenToStandardError(),
      containsString("Cannot specify both -textFile and -dbFile"));
  }

  @Test
  void testREADMEFlag() {
    MainMethodResult result = invokeMain("-README");
    assertThat(result.getTextWrittenToStandardOut(), containsString("Project 4"));
  }
}
