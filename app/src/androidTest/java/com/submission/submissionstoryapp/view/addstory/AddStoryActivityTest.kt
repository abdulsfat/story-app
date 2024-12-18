package com.submission.submissionstoryapp.view.addstory

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import android.content.Intent
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import com.submission.submissionstoryapp.R
import org.hamcrest.CoreMatchers.allOf

class AddStoryActivityTest {

    private lateinit var idlingResource: SimpleIdlingResource

    @Before
    fun setUp() {
        idlingResource = SimpleIdlingResource()
        IdlingRegistry.getInstance().register(idlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        Intents.release()
    }

    @Test
    fun testAddStory() {
        val mockImageUri = Uri.parse("content://com.android.providers.media.documents/document/image%3A62")
        val resultData = Intent().apply {
            data = mockImageUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        intending(allOf(
            hasAction(Intent.ACTION_OPEN_DOCUMENT),
            hasType("image/*")
        )).respondWith(result)

        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AddStoryActivity::class.java)
        ActivityScenario.launch<AddStoryActivity>(intent)

        val permissionResult = Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        intending(allOf(
            hasAction(Intent.ACTION_OPEN_DOCUMENT),
            hasType("image/*")
        )).respondWith(permissionResult)

        onView(withId(R.id.etStoryDescription)).perform(replaceText("Testing story upload"))

        onView(withId(R.id.galleryButton)).perform(click())

        intended(allOf(
            hasAction(Intent.ACTION_OPEN_DOCUMENT),
            hasType("image/*")
        ))

        onView(withId(R.id.uploadButton)).perform(click())

        onView(withText("Story uploaded successfully!")).check(matches(isDisplayed()))

        onView(withText("Story uploaded successfully!")).check(matches(withToastText("Story uploaded successfully!")))
    }
}
