package com.example.phonebill_android.it;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

  private static final String CUSTOMER = "Ui Integration Customer";

  @After
  public void cleanup() {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    context.deleteFile(fileNameFor(CUSTOMER));
  }

  @Test
  public void addCallThenPrettyPrintDisplaysStoredBill() {
    try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
      onView(withId(R.id.customer_name_input)).perform(replaceText(CUSTOMER));
      onView(withId(R.id.caller_number_input)).perform(replaceText("503-555-1111"));
      onView(withId(R.id.callee_number_input)).perform(replaceText("503-555-2222"));
      onView(withId(R.id.begin_time_input)).perform(replaceText("03/10/2026 9:15 PM"));
      onView(withId(R.id.end_time_input)).perform(replaceText("03/10/2026 9:45 PM"));
      closeSoftKeyboard();

      onView(withId(R.id.add_call_button)).perform(click());
      onView(withId(R.id.results_view)).check(matches(withText(org.hamcrest.Matchers.containsString("Added call:"))));

      onView(withId(R.id.pretty_print_button)).perform(click());
      onView(withId(R.id.results_view)).check(matches(withText(org.hamcrest.Matchers.containsString("Phone Bill for: " + CUSTOMER))));
      onView(withId(R.id.results_view)).check(matches(withText(org.hamcrest.Matchers.containsString("503-555-2222"))));
    }
  }

  @Test
  public void addCallWithMalformedCallerShowsValidationDialog() {
    try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
      onView(withId(R.id.customer_name_input)).perform(replaceText(CUSTOMER));
      onView(withId(R.id.caller_number_input)).perform(replaceText("5035551111"));
      onView(withId(R.id.callee_number_input)).perform(replaceText("503-555-2222"));
      onView(withId(R.id.begin_time_input)).perform(replaceText("03/10/2026 9:15 PM"));
      onView(withId(R.id.end_time_input)).perform(replaceText("03/10/2026 9:45 PM"));
      closeSoftKeyboard();

      onView(withId(R.id.add_call_button)).perform(click());

      onView(withText("Invalid caller number format: 5035551111 (expected nnn-nnn-nnnn)"))
          .check(matches(isDisplayed()));
      onView(withText("OK")).perform(click());
    }
  }

  @Test
  public void searchWithoutRangeShowsRequiredInputDialog() {
    try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
      onView(withId(R.id.customer_name_input)).perform(replaceText(CUSTOMER));
      closeSoftKeyboard();

      onView(withId(R.id.search_button)).perform(click());

      onView(withText("Search begin date/time is required"))
          .check(matches(isDisplayed()));
      onView(withText("OK")).perform(click());
    }
  }

  private String fileNameFor(String customer) {
    String normalized = customer.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    if (normalized.isEmpty()) {
      normalized = "customer";
    }

    return "phonebill-" + normalized + ".txt";
  }
}