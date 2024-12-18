package com.submission.submissionstoryapp.view.addstory

import android.widget.Toast
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun withToastText(toastText: String): Matcher<Any> {
    return object : TypeSafeMatcher<Any>() {
        override fun describeTo(description: Description?) {
            description?.appendText("with toast text: $toastText")
        }

        override fun matchesSafely(item: Any?): Boolean {
            return if (item is Toast) {
                val shownText = getToastText(item)
                shownText?.toString() == toastText
            } else {
                false
            }
        }

        private fun getToastText(toast: Toast): CharSequence? {
            return try {
                val field = Toast::class.java.getDeclaredField("mText")
                field.isAccessible = true
                field.get(toast) as? CharSequence
            } catch (e: Exception) {
                null
            }
        }
    }
}
