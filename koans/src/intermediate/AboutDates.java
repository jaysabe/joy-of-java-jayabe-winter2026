package intermediate;

import com.sandwich.koan.Koan;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.sandwich.koan.constant.KoanConstants.__;
import static com.sandwich.util.Assert.assertEquals;


public class AboutDates {

    private LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(100010001000L), ZoneId.of("-07:00"));

    @Koan
    public void dateToString() {
        assertEquals(date.toString(), "1973-03-03T05:33:21");
    }

    @Koan
    public void changingDateValue() {
        date = date.plusHours(1);
        assertEquals(date.toString(), "1973-03-03T06:33:21");
    }

    @Koan
    public void usingPlusToChangeDatesDoesntWrapOtherFields() {
        date = date.plusMonths(12);
        assertEquals(date.toString(), "1974-03-03T06:33:21");
    }

    @Koan
    public void usingDateTimeFormatterToFormatDate() {
        String formattedDate = DateTimeFormatter.ISO_DATE.format(date);
        assertEquals(formattedDate, "1974-03-03");
    }

    @Koan
    public void usingDateTimeFormatterToFormatDateShort() {
        // Careful, formatted dates may contain non-breaking spaces!
        String formattedDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date);
        assertEquals(formattedDate, "3/3/74, 6:33 AM"); // untyped symbols to pass
    }

    @Koan
    public void usingDateTimeFormatterToFormatDateFull() {
        String formattedDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(date);
        // There is also FormatStyle.LONG and FormatStyle.FULL... you get the idea ;-)
        assertEquals(formattedDate, "Mar 3, 1974, 6:33:21 AM");
    }

    @Koan
    public void usingDateTimeFormatterToParseDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate date2 = LocalDate.parse("01-01-2000", formatter);
        assertEquals(date2.toString(), "2000-01-01");
        // What happened to the time? What do you need to change to keep the time as well?
    }
}
