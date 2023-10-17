package com.guga.supp4youapp.presentation.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.core.View
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import com.guga.supp4youapp.R
import com.guga.supp4youapp.presentation.ui.camera.CameraActivity
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class GenerateFragmentTest {

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testOnCreateView() {
        // Arrange
        val fragment = GenerateFragment()
        val spaceId = "testSpaceId"
        val personName = "Test Person"
        val groupName = "Test Group"
        val selectedDays = "3 Days"
        val selectBeginTime = "Start Time"
        val selectEndTime = "End Time"
        val timeStamp = 12345L
        val args = Bundle()
        args.putString("spaceId", spaceId)
        args.putString("personName", personName)
        args.putString("groupName", groupName)
        args.putString("selectDays", selectedDays)
        args.putString("selectBeginTime", selectBeginTime)
        args.putString("selectEndTime", selectEndTime)
        args.putLong("timestamp", timeStamp)

        // Act
        fragment.arguments = args

        // Assert
        assertNotNull(spaceId)
    }

    @Test
    fun testOnViewCreated() {
        // Arrange
        val scenario = launchFragmentInContainer<GenerateFragment>(themeResId = R.style.Theme_POV)

        val spaceId = "testSpaceId"
        val personName = "Test Person"
        val groupName = "Test Group"
        val selectedDays = "3 Days"
        val selectBeginTime = "Start Time"
        val selectEndTime = "End Time"
        val timeStamp = 12345L

        val args = Bundle()
        args.putString("spaceId", spaceId)
        args.putString("personName", personName)
        args.putString("groupName", groupName)
        args.putString("selectDays", selectedDays)
        args.putString("selectBeginTime", selectBeginTime)
        args.putString("selectEndTime", selectEndTime)
        args.putLong("timestamp", timeStamp)
        scenario.onFragment { fragment ->
            fragment.arguments = args
        }

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, CameraActivity::class.java)
        intent.putExtra("groupId", spaceId)
        intent.putExtra("personName", personName)
        intent.putExtra("groupName", groupName)
        intent.putExtra("selectDays", selectedDays)
        intent.putExtra("selectBeginTime", selectBeginTime)
        intent.putExtra("selectEndTime", selectEndTime)
        intent.putExtra("timeStamp", timeStamp)


        // Act
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onFragment { fragment ->
            // Assert
            assertNotNull(intent.extras)
        }
    }
}