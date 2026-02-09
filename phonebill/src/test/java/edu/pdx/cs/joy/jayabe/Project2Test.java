package edu.pdx.cs.joy.jayabe;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
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
        // Crucial for TDD: Restore system streams so other tests aren't affected
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testREADMEOption() {
        String[] args = {"-README"};
        new Project3().parseAndRun(args);
        String output = outContent.toString();
        assertTrue(output.contains("Project 3: Phone Bill Application"));
    }

    @Test
    void testPrintOption() {
        String[] args = {
                "-print", "Alice", "503-123-4567", "503-765-4321",
                "01/27/2026", "10:00", "01/27/2026", "10:30"
        };
        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("503-123-4567"));
        assertThat(output, containsString("503-765-4321"));
    }

    @Test
    void testMissingArgumentsPrintsUsage() {
        String[] args = {};
        Project3.main(args);

        // Error messages usually go to System.err in CLI apps
        String errOutput = errContent.toString();
        String outOutput = outContent.toString();

        assertTrue(errOutput.contains("usage") || outOutput.contains("usage"),
                "Should print usage for empty args");
    }

    @Test
    void whenFileNameIsNullReturnNewBillWithCustomerName() {
        Project3 project = new Project3();
        String customer = "Alice";

        // Assuming handleTextFile is accessible or called via a public method
        PhoneBill bill = project.handleTextFile(null, customer);

        assertThat(bill.getCustomer(), new StringContains(customer));
        assertThat(bill.getPhoneCalls(), hasSize(0));
    }

    @Test
    void whenCustomerNameInFileDoesNotMatchCommandLineThrowException(@TempDir File tempDir) throws IOException {
        Project3 project = new Project3();
        String cmdLineCustomer = "Charlie";
        String fileCustomer = "DifferentName";

        // Create a file with a mismatching name
        File textFile = new File(tempDir, "mismatch.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile))) {
            pw.println(fileCustomer);
        }

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            project.handleTextFile(textFile.getPath(), cmdLineCustomer);
        });

        assertThat(ex.getMessage(), CoreMatchers.containsString("does not match command line"));
    }

    @Test
    void whenFileIsMalformedThrowIllegalArgumentException(@TempDir File tempDir) throws IOException {
        Project3 project = new Project3();
        File textFile = new File(tempDir, "malformed.txt");

        // Create a file with invalid phone call data
        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile))) {
            pw.println("CustomerName");
            pw.println("This is not a valid phone call line");
        }

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            project.handleTextFile(textFile.getPath(), "CustomerName");
        });

        assertThat(ex.getMessage(), containsString("Error processing file"));
    }
}