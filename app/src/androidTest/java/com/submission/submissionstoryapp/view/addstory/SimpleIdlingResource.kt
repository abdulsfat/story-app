package com.submission.submissionstoryapp.view.addstory

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class SimpleIdlingResource : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null
    private val isIdle = AtomicBoolean(true)

    override fun getName(): String = SimpleIdlingResource::class.java.name

    override fun isIdleNow(): Boolean {
        return isIdle.get().also { idle ->
            if (idle) {
                callback?.onTransitionToIdle()
            }
        }
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    fun setIdleState(isIdleNow: Boolean) {
        isIdle.set(isIdleNow)
        if (isIdleNow) {
            callback?.onTransitionToIdle()
        }
    }
}

