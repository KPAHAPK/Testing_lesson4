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

        val btnSearch = uiDevice.findObject(By.res(packageName, "btn_search"))
        btnSearch.click()

        val changedText = uiDevice.wait(Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT)
        Assert.assertEquals(changedText.text.toString(), "Number of results: 701")
    }

    @Test
    fun test_OpenDetailsScreen(): Unit {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"

        val btnSearch = uiDevice.findObject(By.res(packageName, "btn_search"))
        btnSearch.click()

        val textView: UiObject2 =
            uiDevice.wait(Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT)
        val amountOfRepositories = textView.text.toString().filter { it.isDigit() }

        val toDetails: UiObject2 =
            uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.clickAndWait(Until.newWindow(), 5000L)

        val detailsTextView = uiDevice.findObject(By.res(packageName, "totalCountTextView"))
        val detailsScreenAmountOfRepositories =
            detailsTextView.text.toString().filter { it.isDigit() }
        Log.d("UIAutomator", detailsScreenAmountOfRepositories)
        Assert.assertEquals(detailsScreenAmountOfRepositories, amountOfRepositories)
    }

//    @Test
//    fun test_DetailsScreenIncrementButton(){
//        val btn
//    }
}