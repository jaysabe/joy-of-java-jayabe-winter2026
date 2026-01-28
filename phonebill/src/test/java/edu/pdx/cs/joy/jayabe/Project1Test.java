package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.jayabe.PhoneBill;
import edu.pdx.cs.joy.jayabe.PhoneCall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class Project1Test {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  private void resetStreams() {
    outContent.reset();
    errContent.reset();
  }

  @Test
  void testMissingArguments() {
    String[] args = {};
    new Project1().parseAndRun(args);
    String output = outContent.toString();
    assertTrue(output.contains("usage"), "Should print usage for empty args");
  }

  @Test
  void testTooFewArguments() {
    String[] args = {"Alice", "503-123-4567"};
    Project1.main(args);
    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("Missing callee phone number"));
    assertTrue(errOutput.contains("usage"));
  }

  @Test
  void testTooManyArguments() {
    String[] args = {
            "Alice", "503-123-4567", "503-765-4321",
            "01/27/2026", "10:00", "01/27/2026", "10:30",
            "extraArg"
    };
    Project1.main(args);
    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("Too many command line arguments"));
    assertTrue(errOutput.contains("usage"));
  }

  @Test
  void testInvalidCallerNumber() {
    String[] args = {
            "Alice", "5031234567", "503-765-4321",
            "01/27/2026", "10:00", "01/27/2026", "10:30"
    };
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> Project1.main(args));
    assertTrue(ex.getMessage().contains("Invalid caller number format"));
  }

  @Test
  void testInvalidCalleeNumber() {
    String[] args = {
            "Alice", "503-123-4567", "5037654321",
            "01/27/2026", "10:00", "01/27/2026", "10:30"
    };
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> Project1.main(args));
    assertTrue(ex.getMessage().contains("Invalid callee number format"));
  }

  @Test
  void testInvalidBeginTime() {
    String[] args = {
            "Alice", "503-123-4567", "503-765-4321",
            "01/27/2026", "1000", "01/27/2026", "10:30"
    };
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> Project1.main(args));
    assertTrue(ex.getMessage().contains("Invalid begin time format"));
  }

  @Test
  void testInvalidEndTime() {
    String[] args = {
            "Alice", "503-123-4567", "503-765-4321",
            "01/27/2026", "10:00", "01/27/2026", "1030"
    };
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> Project1.main(args));
    assertTrue(ex.getMessage().contains("Invalid end time format"));
  }

  @Test
  void testPrintOption() {
    String[] args = {
            "-print", "Alice", "503-123-4567", "503-765-4321",
            "01/27/2026", "10:00", "01/27/2026", "10:30"
    };
    Project1.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("Alice"));
    assertTrue(output.contains("503-123-4567"));
  }

  @Test
  void testREADMEOption() {
    String[] args = {"-README"};
    Project1.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("Project 1: Phone Bill Application"));
  }
}
