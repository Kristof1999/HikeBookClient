package hu.kristof.nagy.hikebookclient.util

import androidx.test.espresso.IdlingRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class DataBindingIdlingResourceRule(
    private val dataBindingIdlingResource: DataBindingIdlingResource
) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
}