package com.example.phonebill_android.test;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.phonebill_android.MainActivity;
import com.example.phonebill_android.R;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityEndToEndTest {

  private static final String CUSTOMER = "End To End Customer";

  @After
  public void cleanup() {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    context.deleteFile(fileNameFor(CUSTOMER));
  }

  @Test
  public void addCallsRestartAppAndSearchPersistedResults() {
    try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
      addCall(
          CUSTOMER,
          "503-555-1111",
          "503-555-2222",
          "03/12/2026 8:00 AM",
          "03/12/2026 8:30 AM");

      addCall(
          CUSTOMER,
          "503-555-1111",
          "503-555-3333",
          "03/12/2026 10:00 AM",
          "03/12/2026 10:20 AM");
    }

    try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
      onView(withId(R.id.customer_name_input)).perform(replaceText(CUSTOMER));
      onView(withId(R.id.search_begin_input)).perform(scrollTo(), replaceText("03/12/2026 9:00 AM"));
      onView(withId(R.id.search_end_input)).perform(scrollTo(), replaceText("03/12/2026 11:00 AM"));
      closeSoftKeyboard();

      onView(withId(R.id.search_button)).perform(scrollTo(), click());

      onView(withId(R.id.results_view))
          .perform(scrollTo())
          .check(matches(withText(org.hamcrest.Matchers.containsString("Phone Bill for: " + CUSTOMER))));
      onView(withId(R.id.results_view))
          .perform(scrollTo())
          .check(matches(withText(org.hamcrest.Matchers.containsString("503-555-3333"))));
      onView(withId(R.id.results_view))
          .perform(scrollTo())
          .check(matches(withText(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("503-555-2222")))));

      onView(withId(R.id.pretty_print_button)).perform(scrollTo(), click());

      onView(withId(R.id.results_view))
          .perform(scrollTo())
          .check(matches(withText(org.hamcrest.Matchers.containsString("503-555-2222"))));
      onView(withId(R.id.results_view))
          .perform(scrollTo())
          .check(matches(withText(org.hamcrest.Matchers.containsString("503-555-3333"))));
    }
  }

  @Test
  public void searchWithInvertedRangeShowsValidationDialog() {
    try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
      onView(withId(R.id.customer_name_input)).perform(replaceText(CUSTOMER));
      onView(withId(R.id.search_begin_input)).perform(scrollTo(), replaceText("03/12/2026 11:00 AM"));
      onView(withId(R.id.search_end_input)).perform(scrollTo(), replaceText("03/12/2026 9:00 AM"));
      closeSoftKeyboard();

      onView(withId(R.id.search_button)).perform(scrollTo(), click());

      onView(withText("Search end time cannot be before begin time"))
          .check(matches(isDisplayed()));
      onView(withText("OK")).perform(click());
    }
  }

  private void addCall(String customer, String caller, String callee, String begin, String end) {
    onView(withId(R.id.customer_name_input)).perform(replaceText(customer));
    onView(withId(R.id.caller_number_input)).perform(replaceText(caller));
    onView(withId(R.id.callee_number_input)).perform(replaceText(callee));
    onView(withId(R.id.begin_time_input)).perform(replaceText(begin));
    onView(withId(R.id.end_time_input)).perform(replaceText(end));
    closeSoftKeyboard();

    onView(withId(R.id.add_call_button)).perform(scrollTo(), click());
    onView(withId(R.id.results_view))
        .perform(scrollTo())
        .check(matches(withText(org.hamcrest.Matchers.containsString("Added call:"))));
  }

  private String fileNameFor(String customer) {
    String normalized = customer.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    if (normalized.isEmpty()) {
      normalized = "customer";
    }

    return "phonebill-" + normalized + ".txt";
  }
}