package com.example.phonebill_android;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class PhoneBillService {
  public static final DateTimeFormatter CLI_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

  private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

  public PhoneCall createPhoneCall(String customer, String caller, String callee,
                                   String beginText, String endText) {
    if (isBlank(customer)) {
      throw new IllegalArgumentException("Customer name is required");
    }

    validatePhoneNumber(caller, "caller number");
    validatePhoneNumber(callee, "callee number");

    LocalDateTime begin = parseCliDateTime(beginText, "begin");
    LocalDateTime end = parseCliDateTime(endText, "end");
    if (end.isBefore(begin)) {
      throw new IllegalArgumentException("End time cannot be before begin time");
    }

    return new PhoneCall(customer.trim(), caller.trim(), callee.trim(), begin, end);
  }

  public LocalDateTime parseCliDateTime(String text, String field) {
    if (isBlank(text)) {
      throw new IllegalArgumentException(field + " date/time is required");
    }

    try {
      return LocalDateTime.parse(text.trim(), CLI_DATE_TIME_FORMATTER);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(
          "Invalid " + field + " date/time format: " + text
              + " (expected MM/dd/yyyy h:mm a)");
    }
  }

  public List<PhoneCall> searchCallsBetween(PhoneBill bill, LocalDateTime begin, LocalDateTime end) {
    if (end.isBefore(begin)) {
      throw new IllegalArgumentException("Search end time cannot be before begin time");
    }

    List<PhoneCall> matches = new ArrayList<>();
    for (PhoneCall call : bill.getPhoneCalls()) {
      LocalDateTime callBegin = call.getBeginTime();
      boolean onOrAfterBegin = callBegin.isEqual(begin) || callBegin.isAfter(begin);
      boolean onOrBeforeEnd = callBegin.isEqual(end) || callBegin.isBefore(end);
      if (onOrAfterBegin && onOrBeforeEnd) {
        matches.add(call);
      }
    }

    return matches;
  }

  private void validatePhoneNumber(String number, String field) {
    if (isBlank(number) || !PHONE_PATTERN.matcher(number.trim()).matches()) {
      throw new IllegalArgumentException(
          "Invalid " + field + " format: " + number + " (expected nnn-nnn-nnnn)");
    }
  }

  private boolean isBlank(String text) {
    return text == null || text.trim().isEmpty();
  }
}
