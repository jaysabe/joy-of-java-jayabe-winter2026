package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class Project3Test {
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
    void testREADMEOption() {
        String[] args = {"-README"};
        Project3.main(args);
        String output = outContent.toString();
        assertTrue(output.contains("Project 3") || output.contains("Phone Bill"),
                "README output should contain project information");
    }

    @Test
    void testPrintOption() {
        String[] args = {
                "-print", "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project3.main(args);

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
        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("Alice"));
        assertThat(output, containsString("503-123-4567"));
        assertThat(output, containsString("Duration"));
    }

    @Test
    void testPrettyPrintToFile(@TempDir File tempDir) {
        File prettyFile = new File(tempDir, "pretty.txt");
        
        String[] args = {
                "-pretty", prettyFile.getAbsolutePath(),
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project3.main(args);

        assertTrue(prettyFile.exists(), "Pretty print file should be created");
    }

    @Test
    void testMissingArgumentsPrintsUsage() {
        String[] args = {};
        Project3.main(args);

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
        Project3.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("End time cannot be before begin time"),
                "Should error when end time is before begin time");
    }

    @Test
    void whenCustomerNameInFileDoesNotMatchCommandLineThrowException(@TempDir File tempDir) throws IOException {
        String cmdLineCustomer = "Charlie";
        String fileCustomer = "DifferentName";

        File textFile = new File(tempDir, "mismatch.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile))) {
            pw.println(fileCustomer);
        }

        String[] args = {"-textFile", textFile.getAbsolutePath(), cmdLineCustomer, 
                         "503-123-4567", "503-765-4321", "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"};
        Project3.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("does not match") || errOutput.contains("Customer"),
                "Should error when customer names don't match");
    }

    @Test
    void whenFileIsMalformedThrowIllegalArgumentException(@TempDir File tempDir) throws IOException {
        File textFile = new File(tempDir, "malformed.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile))) {
            pw.println("CustomerName");
            pw.println("This is not a valid phone call line");
        }

        String[] args = {"-textFile", textFile.getAbsolutePath(), "CustomerName",
                         "503-123-4567", "503-765-4321", "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"};
        Project3.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Error") || errOutput.contains("format"),
                "Should error for malformed file");
    }

    @Test
    void testValidPhoneCallAddition() {
        String[] args = {
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project3.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.isEmpty() || !errOutput.contains("Error"),
                "Should complete without errors for valid arguments");
    }

    @Test
    void testCreateNewFileWhenItDoesNotExist(@TempDir File tempDir) {
        File textFile = new File(tempDir, "newbill.txt");
        
        String[] args = {
                "-textFile", textFile.getAbsolutePath(),
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "AM", "01/27/2026", "10:30", "AM"
        };
        Project3.main(args);

        assertTrue(textFile.exists(), "File should be created if it doesn't exist");
    }

    @Test
    void testInvalidDateTimeFormat() {
        String[] args = {
                "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "25:00", "XM", "01/27/2026", "10:30", "AM"
        };
        Project3.main(args);

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Invalid") || errOutput.contains("format"),
                "Should error for invalid date/time format");
    }
}
