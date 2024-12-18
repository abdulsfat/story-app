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
        // Initialize IdlingResource and Espresso Intents
        idlingResource = SimpleIdlingResource()
        IdlingRegistry.getInstance().register(idlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        // Unregister IdlingResource and release Espresso Intents
        IdlingRegistry.getInstance().unregister(idlingResource)
        Intents.release()
    }

    @Test
    fun testAddStory() {
        // Mock data URI for the image
        val mockImageUri = Uri.parse("content://com.android.providers.media.documents/document/image%3A62")
        val resultData = Intent().apply {
            data = mockImageUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        // Stub Intent with ACTION_OPEN_DOCUMENT
        intending(allOf(
            hasAction(Intent.ACTION_OPEN_DOCUMENT),
            hasType("image/*")
        )).respondWith(result)

        // Launch the activity
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AddStoryActivity::class.java)
        ActivityScenario.launch<AddStoryActivity>(intent)

        // Simulate permission request (mock permission granted)
        val permissionResult = Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        intending(allOf(
            hasAction(Intent.ACTION_OPEN_DOCUMENT),
            hasType("image/*")
        )).respondWith(permissionResult)

        // Fill in the description text
        onView(withId(R.id.etStoryDescription)).perform(replaceText("Testing story upload"))

        // Click on the gallery button
        onView(withId(R.id.galleryButton)).perform(click())

        // Verify that the gallery intent is launched
        intended(allOf(
            hasAction(Intent.ACTION_OPEN_DOCUMENT),
            hasType("image/*")
        ))

        // Click on the upload button
        onView(withId(R.id.uploadButton)).perform(click())

        // Verify that the "Story uploaded successfully!" message is displayed
        onView(withText("Story uploaded successfully!")).check(matches(isDisplayed()))

        // Verify Toast message with custom matcher
        onView(withText("Story uploaded successfully!")).check(matches(withToastText("Story uploaded successfully!")))
    }
}
