package com.rivaldofez.storymessage

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rivaldofez.storymessage.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class BaseActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activity = ActivityScenarioRule(BaseActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }


    @Test
    fun loadStoryDetailInformation() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_story))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.rv_story)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                ViewActions.click()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.toolbar_detail_story))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(ViewMatchers.withId(R.id.tv_name))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.tv_date))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.img_story))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.tv_description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.pressBack()

    }
}