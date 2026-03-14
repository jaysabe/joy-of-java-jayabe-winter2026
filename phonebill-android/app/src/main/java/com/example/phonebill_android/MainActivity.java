package com.example.phonebill_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private final PhoneBillService service = new PhoneBillService();
  private final PhoneBillRepository repository = new PhoneBillRepository();

  private EditText customerName;
  private EditText callerNumber;
  private EditText calleeNumber;
  private EditText beginDateTime;
  private EditText endDateTime;
  private EditText searchBegin;
  private EditText searchEnd;
  private TextView resultsView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    this.customerName = findViewById(R.id.customer_name_input);
    this.callerNumber = findViewById(R.id.caller_number_input);
    this.calleeNumber = findViewById(R.id.callee_number_input);
    this.beginDateTime = findViewById(R.id.begin_time_input);
    this.endDateTime = findViewById(R.id.end_time_input);
    this.searchBegin = findViewById(R.id.search_begin_input);
    this.searchEnd = findViewById(R.id.search_end_input);
    this.resultsView = findViewById(R.id.results_view);

    Button addCallButton = findViewById(R.id.add_call_button);
    Button prettyPrintButton = findViewById(R.id.pretty_print_button);
    Button searchButton = findViewById(R.id.search_button);

    addCallButton.setOnClickListener(view -> addCall());
    prettyPrintButton.setOnClickListener(view -> showPrettyBill());
    searchButton.setOnClickListener(view -> searchCalls());
  }

  @Override
  public boolean onCreateOptionsMenu(android.view.Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
    if (item.getItemId() == R.id.action_readme) {
      new AlertDialog.Builder(this)
          .setTitle(R.string.readme_title)
          .setMessage(R.string.readme_text)
          .setPositiveButton(R.string.ok, null)
          .show();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void addCall() {
    try {
      String customer = requiredText(this.customerName, "Customer name is required");
      String caller = requiredText(this.callerNumber, "Caller number is required");
      String callee = requiredText(this.calleeNumber, "Callee number is required");
      String begin = requiredText(this.beginDateTime, "Begin date/time is required");
      String end = requiredText(this.endDateTime, "End date/time is required");

      PhoneCall call = this.service.createPhoneCall(customer, caller, callee, begin, end);
      PhoneBill bill = this.repository.loadBill(this, customer);
      bill.addPhoneCall(call);
      this.repository.saveBill(this, bill);

      this.resultsView.setText("Added call:\n" + call);
    } catch (Exception ex) {
      showError(ex.getMessage());
    }
  }

  private void showPrettyBill() {
    try {
      String customer = requiredText(this.customerName, "Customer name is required");
      PhoneBill bill = this.repository.loadBill(this, customer);

      StringWriter writer = new StringWriter();
      new PrettyPrinter(writer).dump(bill);
      this.resultsView.setText(writer.toString());
    } catch (Exception ex) {
      showError(ex.getMessage());
    }
  }

  private void searchCalls() {
    try {
      String customer = requiredText(this.customerName, "Customer name is required");
      String searchBeginText = requiredText(this.searchBegin, "Search begin date/time is required");
      String searchEndText = requiredText(this.searchEnd, "Search end date/time is required");

      LocalDateTime begin = this.service.parseCliDateTime(searchBeginText, "search begin");
      LocalDateTime end = this.service.parseCliDateTime(searchEndText, "search end");

      PhoneBill bill = this.repository.loadBill(this, customer);
      List<PhoneCall> matchingCalls = this.service.searchCallsBetween(bill, begin, end);

      PhoneBill filtered = new PhoneBill(customer);
      for (PhoneCall call : matchingCalls) {
        filtered.addPhoneCall(call);
      }

      StringWriter writer = new StringWriter();
      new PrettyPrinter(writer).dump(filtered);
      this.resultsView.setText("Search results between " + searchBeginText + " and " + searchEndText
          + "\n\n" + writer);
    } catch (Exception ex) {
      showError(ex.getMessage());
    }
  }

  private String requiredText(EditText field, String errorMessage) {
    String value = field.getText().toString();
    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value.trim();
  }

  private void showError(String message) {
    String text = message == null || message.trim().isEmpty() ? "Unexpected error" : message;
    new AlertDialog.Builder(this)
        .setTitle(R.string.error_title)
        .setMessage(text)
        .setPositiveButton(R.string.ok, null)
        .show();
  }
}
