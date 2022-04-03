package com.geekbrains.tests.automator

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class DetailsTest {

    private val uiDevice = UiDevice.getInstance(getInstrumentation())
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageName = context.packageName
    companion object{
        private const val TIMEOUT = 5000L
    }

    @Before
    fun setUp() {
        uiDevice.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.clickAndWait(Until.newWindow(), TIMEOUT)
    }

    @Test
    fun test_DetailsScreenIncrementButton() {
        val textView = uiDevice.findObject(By.res(packageName, "totalCountTextView"))
        val amountOfRepositories = textView.text.toString().filter { it.isDigit() }
        val btnIncrement = uiDevice.findObject(By.res(packageName, "incrementButton"))
        btnIncrement.click()

        uiDevice.waitForIdle(TIMEOUT)

        Assert.assertEquals(textView.text.toString(),
            "Number of results: ${amountOfRepositories.toInt() + 1}")
    }
    @Test
    fun test_DetailsScreenDecrementButton() {
        val textView = uiDevice.findObject(By.res(packageName, "totalCountTextView"))
        val amountOfRepositories = textView.text.toString().filter { it.isDigit() }
        val btnDecrement = uiDevice.findObject(By.res(packageName, "decrementButton"))
        btnDecrement.click()

        uiDevice.waitForIdle(TIMEOUT)

        Assert.assertEquals(textView.text.toString(),
            "Number of results: ${amountOfRepositories.toInt() - 1}")
    }
}