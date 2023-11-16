package com.guga.supp4youapp.presentation.ui.fragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.guga.supp4youapp.data.remote.database.Space
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AccessFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        MockitoAnnotations.initMocks(this)

    }

    @Test
    fun testCreateSpace() {
        // Arrange
        val accessFragment = AccessFragment()
        val space = Space(0L, "3 Days", "Start Time", "End Time", "12345", 10)
        val space2 = Space(0L, "3 Days", "Start Time", "End Time", "12345", 10)

        runBlocking {
            // Act
            val result = accessFragment.createSpace(space)

            // Assert
            assertNotNull(result)
            assertNotSame(space, space2)
        }
    }

}


