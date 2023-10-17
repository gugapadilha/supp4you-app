package com.guga.supp4youapp.presentation.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.guga.supp4youapp.R
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DetailsFragmentTest {

    private val testPhotoName = "test_photo.jpg"
    private val testPersonName = "John Doe"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testTakePhotoAndGetUri() {
        //Ararnge
        val scenario = launchFragment<DetailsFragment>()

        scenario.onFragment { fragment ->
            val childFragmentManager = mock(FragmentManager::class.java) // Crie um mock para childFragmentManager
            val fragmentTransaction = mock(FragmentTransaction::class.java) // Crie um mock para fragmentTransaction

            val bottomSheetFragment = DetailsFragment.MyBottomSheetDialogFragment() // Crie uma instância real

            //Act
            `when`(childFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
            `when`(childFragmentManager.findFragmentByTag(DetailsFragment.MyBottomSheetDialogFragment::class.java.simpleName)).thenReturn(bottomSheetFragment)

            val args = Bundle()
            args.putString("personName", testPersonName)
            args.putString("photoName", testPhotoName)
            args.putParcelable("takenPhotoUri", null) // Configure a URI desejada
            bottomSheetFragment.arguments = args

            fragment.takePhotoAndGetUri(testPhotoName)

            //Assert
            verify(fragmentTransaction)
        }
    }

    @Test
    fun testOnResumeWithSavedName() {

        //Arrange
        val sharedPreferences = mock(SharedPreferences::class.java)
        val editor = mock(SharedPreferences.Editor::class.java)
        val callback = mock(OnBackPressedCallback::class.java)

        `when`(sharedPreferences.getString("personName", "")).thenReturn(testPersonName)

        `when`(editor.putString(eq("personName"), eq(testPersonName))).thenReturn(editor)
        `when`(sharedPreferences.edit()).thenReturn(editor)

        val scenario = launchFragment<DetailsFragment>()

        //Act
        scenario.onFragment { fragment ->
            val context = mock(Context::class.java)

            `when`(context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)

            val activity = mock(FragmentActivity::class.java)
            fragment.onAttach(context)
            fragment.onAttach(activity)

            //Assert
            assertNotNull(activity)
            assertNotNull(context)
        }
    }

    @Test
    fun testOnDestroyView() {

        //Arrange
        val scenario = launchFragment<DetailsFragment>()

        //Act
        scenario.onFragment { fragment ->
            val initialBinding = fragment._binding

            fragment.onDestroyView()

            //Assert
            assert(fragment._binding == null)
            assertTrue(initialBinding != null)
        }
    }

    @Test
    fun testOnPauseView() {

        //Arrange
        val scenario = launchFragment<DetailsFragment>()

        //Act
        scenario.onFragment { fragment ->
            val initialBinding = fragment._binding

            fragment.onPause()

            //Assert
            assertTrue(initialBinding != null)
        }
    }



}





