Project 3: Pretty Printing A Phone Bill
Author: Jay Abegglen

This program creates and manages phone bills with phone call records.
Information is provided via command-line arguments.

== Features ==

* Phone calls are sorted chronologically by begin time
* If two phone calls begin at the same time, they are sorted by caller's phone number
* Support for reading/writing phone bills to text files
* Pretty printing phone bills with formatted output including call duration in minutes
* Date and time validation with AM/PM format
* Customer name validation across file operations

== Usage ==

java -jar target/phonebill-1.0.0.jar [options] <args>

Arguments (in order):
  customer       Person whose phone bill we're modeling
  callerNumber   Phone number of caller (format: nnn-nnn-nnnn)
  calleeNumber   Phone number of person who was called (format: nnn-nnn-nnnn)
  begin          Date and time call began (format: mm/dd/yyyy h:mm am/pm)
  end            Date and time call ended (format: mm/dd/yyyy h:mm am/pm)

Options (may appear in any order):
  -pretty file   Pretty print the phone bill to a text file or standard out (-)
  -textFile file Where to read/write the phone bill
  -print         Prints a description of the new phone call
  -README        Prints this README and exits

== Examples ==

# Create a phone call and print it
java -jar target/phonebill-1.0.0.jar -print "John Doe" 503-123-4567 503-765-4321 "01/15/2026 10:30 AM" "01/15/2026 11:00 AM"

# Save phone bill to file
java -jar target/phonebill-1.0.0.jar -textFile phonebill.txt "John Doe" 503-123-4567 503-765-4321 "01/15/2026 10:30 AM" "01/15/2026 11:00 AM"

# Pretty print to standard output
java -jar target/phonebill-1.0.0.jar -pretty - "John Doe" 503-123-4567 503-765-4321 "01/15/2026 10:30 AM" "01/15/2026 11:00 AM"

# Pretty print to file
java -jar target/phonebill-1.0.0.jar -pretty pretty.txt "John Doe" 503-123-4567 503-765-4321 "01/15/2026 10:30 AM" "01/15/2026 11:00 AM"

== Date and Time Format ==

Date and time must be provided in the format: mm/dd/yyyy h:mm am/pm
Example: 01/02/2026 9:16 PM

Note: The date and time are specified as THREE command-line arguments:
  1. Date (mm/dd/yyyy)
  2. Time (h:mm)
  3. AM/PM indicator

== Error Handling ==

The program validates:
* Required command-line arguments are present
* Phone numbers match format nnn-nnn-nnnn
* Date and time format is correct
* End time is not before begin time
* Customer name in file matches command line (when using -textFile)
