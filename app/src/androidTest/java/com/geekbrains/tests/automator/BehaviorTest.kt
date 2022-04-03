package com.geekbrains.tests.automator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.geekbrains.tests.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class BehaviorTest {
    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val packageName = context.packageName

    companion object {
        private const val TIMEOUT = 5000L
    }

    @Before
    fun setUp(): Unit {
        uiDevice.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
    }

    @Test
    fun test_MainActivityIsStarted(): Unit {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        Assert.assertNotNull(editText)
    }

    @Test
    fun test_SearchIsPositive(): Unit {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"

        Espresso.onView(ViewMatchers.withId(R.id.searchEditText))
            .perform(ViewActions.pressImeActionButton())

        val changedText = uiDevice.wait(Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT)
        Assert.assertEquals(changedText.text.toString(), "Number of result: 668")
    }

    @Test
    fun test_OpenDetailsScreen(): Unit {
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()

    }
}