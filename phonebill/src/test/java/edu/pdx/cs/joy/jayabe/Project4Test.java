package edu.pdx.cs.joy.jayabe;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.jdbc.H2DatabaseHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Project4 class.
 * Tests command-line interface including database persistence functionality.
 */
class Project4Test {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testREADMEOptionContainsProject4Info() {
        String[] args = {"-README"};
        Project4.main(args);
        String output = outContent.toString();
        assertTrue(output.contains("Project 4") || output.contains("Relational Database"),
                "README should mention Project 4 or database");
    }

    @Test
    void testPrintOption() {
        String[] args = {
                "-print", "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("503-123-4567"));
        assertThat(output, containsString("503-765-4321"));
    }

    @Test
    void testPrettyPrintToStandardOut() {
        String[] args = {
                "-pretty", "-", "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("Alice"));
        assertThat(output, containsString("503-123-4567"));
        assertThat(output, containsString("Duration"));
    }

    @Test
    void testDbFileOptionCreatesDatabase(@TempDir File tempDir) throws SQLException, ParserException {
        File dbFile = new File(tempDir, "phonebill.db");
        
        String[] args = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        // Verify database was created and contains data
        try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
            JDBCLoader loader = new JDBCLoader(connection, "Alice");
            PhoneBill bill = loader.parse();
            
            assertThat(bill.getCustomer(), containsString("Alice"));
            assertEquals(1, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testDbFileLoadsExistingData(@TempDir File tempDir) throws SQLException, IOException, ParserException {
        File dbFile = new File(tempDir, "phonebill.db");
        
        // First call: create database with one call
        String[] args1 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Bob", "503-111-2222", "503-333-4444",
                "01/27/2026", "09:00", "AM", "01/27/2026", "09:30", "AM"
        };
        Project4.main(args1);

        // Second call: add another call to same customer
        String[] args2 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Bob", "503-555-6666", "503-777-8888",
                "01/27/2026", "2:00", "PM", "01/27/2026", "2:45", "PM"
        };
        Project4.main(args2);

        // Verify both calls are in database
        try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
            JDBCLoader loader = new JDBCLoader(connection, "Bob");
            PhoneBill bill = loader.parse();
            
            assertEquals(2, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testCannotSpecifyBothTextFileAndDbFile(@TempDir File tempDir) {
        File textFile = new File(tempDir, "phonebill.txt");
        File dbFile = new File(tempDir, "phonebill.db");
        
        String[] args = {
                "-textFile", textFile.getAbsolutePath(),
                "-dbFile", dbFile.getAbsolutePath(),
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Cannot specify both") || errOutput.contains("textFile and -dbFile"),
                "Should error when both -textFile and -dbFile are specified");
    }

    @Test
    void testTextFileStillWorks(@TempDir File tempDir) throws IOException, ParserException {
        File textFile = new File(tempDir, "phonebill.txt");
        
        String[] args = {
                "-textFile", textFile.getAbsolutePath(),
                "Charlie", "503-234-5678", "503-876-5432",
                "01/27/2026", "11:00", "AM", "01/27/2026", "11:30", "AM"
        };
        Project4.main(args);

        assertTrue(textFile.exists(), "Text file should be created");

        // Verify content
        try (FileReader reader = new FileReader(textFile)) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            
            assertEquals("Charlie", bill.getCustomer());
            assertEquals(1, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testDbFileWithPrettyPrint(@TempDir File tempDir) throws SQLException, ParserException {
        File dbFile = new File(tempDir, "phonebill.db");
        File prettyFile = new File(tempDir, "pretty.txt");
        
        String[] args = {
                "-dbFile", dbFile.getAbsolutePath(),
                "-pretty", prettyFile.getAbsolutePath(),
                "David", "503-345-6789", "503-987-6543",
                "01/27/2026", "3:00", "PM", "01/27/2026", "3:45", "PM"
        };
        Project4.main(args);

        // Verify database was created
        try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
            JDBCLoader loader = new JDBCLoader(connection, "David");
            PhoneBill bill = loader.parse();
            
            assertEquals(1, bill.getPhoneCalls().size());
        }

        // Verify pretty file was created
        assertTrue(prettyFile.exists(), "Pretty file should be created");
    }

    @Test
    void testDbFileWithPrintOption(@TempDir File tempDir) {
        File dbFile = new File(tempDir, "phonebill.db");
        
        String[] args = {
                "-dbFile", dbFile.getAbsolutePath(),
                "-print",
                "Eve", "503-456-7890", "503-098-7654",
                "01/27/2026", "4:00", "PM", "01/27/2026", "4:30", "PM"
        };
        Project4.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("503-456-7890"));
        assertThat(output, containsString("503-098-7654"));
    }

    @Test
    void testMissingArgumentsPrintsUsage() {
        String[] args = {};
        Project4.main(args);

        String errOutput = errContent.toString();
        String outOutput = outContent.toString();

        assertTrue(errOutput.contains("usage") || errOutput.contains("required") || outOutput.contains("usage"),
                "Should print usage for empty args");
    }

    @Test
    void testEndTimeBeforeBeginTime() {
        String[] args = {
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "09:00", "AM"
        };
        Project4.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("End time cannot be before begin time"),
                "Should error when end time is before begin time");
    }

    @Test
    void testInvalidPhoneNumberFormat() {
        String[] args = {
                "Alice", "503-INVALID", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Invalid") || errOutput.contains("format"),
                "Should error for invalid phone number format");
    }

    @Test
    void testInvalidDateTimeFormat() {
        String[] args = {
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "25:00", "XM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Invalid") || errOutput.contains("format"),
                "Should error for invalid date/time format");
    }

    @Test
    void testUsageMentionsDbFileOption() {
        String[] args = {"-unknown"};
        Project4.main(args);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("-dbFile") || output.contains("database"),
                "Usage should mention -dbFile option");
    }

    @Test
    void testUsageShowsTextFileAndDbFileAreMutuallyExclusive() {
        String[] args = {"-README"};
        Project4.main(args);
        
        // Check README or run with no args to see usage
        String[] noArgs = {};
        Project4.main(noArgs);
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("textFile") && output.contains("dbFile"),
                "Should mention both -textFile and -dbFile options");
    }

    @Test
    void testValidPhoneCallAdditionWithoutPersistence() {
        String[] args = {
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project4.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.isEmpty() || !errOutput.contains("Error"),
                "Should complete without errors for valid arguments");
    }

    @Test
    void testMultipleCallsSameDatabaseFile(@TempDir File tempDir) throws SQLException, ParserException {
        File dbFile = new File(tempDir, "phonebill.db");
        
        // Add three calls for the same customer
        String[] args1 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Frank", "503-111-1111", "503-222-2222",
                "01/27/2026", "08:00", "AM", "01/27/2026", "08:30", "AM"
        };
        Project4.main(args1);

        String[] args2 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Frank", "503-333-3333", "503-444-4444",
                "01/27/2026", "12:00", "PM", "01/27/2026", "12:45", "PM"
        };
        Project4.main(args2);

        String[] args3 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Frank", "503-555-5555", "503-666-6666",
                "01/27/2026", "5:00", "PM", "01/27/2026", "5:30", "PM"
        };
        Project4.main(args3);

        // Verify all three calls are in database
        try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
            JDBCLoader loader = new JDBCLoader(connection, "Frank");
            PhoneBill bill = loader.parse();
            
            assertEquals(3, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testDatabaseSupportsMultipleCustomers(@TempDir File tempDir) throws SQLException, ParserException {
        File dbFile = new File(tempDir, "phonebill.db");
        
        // Add call for customer 1
        String[] args1 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Grace", "503-111-2222", "503-333-4444",
                "01/27/2026", "09:00", "AM", "01/27/2026", "09:30", "AM"
        };
        Project4.main(args1);

        // Add call for customer 2
        String[] args2 = {
                "-dbFile", dbFile.getAbsolutePath(),
                "Henry", "503-555-6666", "503-777-8888",
                "01/27/2026", "1:00", "PM", "01/27/2026", "1:45", "PM"
        };
        Project4.main(args2);

        // Verify both customers have their own calls
        try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
            JDBCLoader loader1 = new JDBCLoader(connection, "Grace");
            PhoneBill bill1 = loader1.parse();
            assertEquals(1, bill1.getPhoneCalls().size());

            JDBCLoader loader2 = new JDBCLoader(connection, "Henry");
            PhoneBill bill2 = loader2.parse();
            assertEquals(1, bill2.getPhoneCalls().size());
        }
    }
}
