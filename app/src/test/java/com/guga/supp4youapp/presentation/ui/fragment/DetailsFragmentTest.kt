package com.guga.supp4youapp.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
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
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w360dp-h640dp-xhdpi")
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
            val childFragmentManager = mock(FragmentManager::class.java)
            val fragmentTransaction = mock(FragmentTransaction::class.java)

            val bottomSheetFragment = DetailsFragment.MyBottomSheetDialogFragment()

            //Act
            `when`(childFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
            `when`(childFragmentManager.findFragmentByTag(DetailsFragment.MyBottomSheetDialogFragment::class.java.simpleName)).thenReturn(bottomSheetFragment)

            val args = Bundle()
            args.putString("personName", testPersonName)
            args.putString("photoName", testPhotoName)
            args.putParcelable("takenPhotoUri", null)
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
    fun testShowSignOutConfirmationDialogExists() {
        //Arrange
        val fragmentScenario = FragmentScenario.launchInContainer(DetailsFragment::class.java, null, R.style.Theme_POV, null)

        //Act
        fragmentScenario.onFragment { fragment ->
            fragment.showSignOutConfirmationDialog()

            //Assert
            assertTrue(fragment.isSignOutDialogShowing)
        }
    }

    @Test
    fun testOnViewCreated() {
        //Arrange
        val fragmentScenario = FragmentScenario.launchInContainer(DetailsFragment::class.java, null, R.style.Theme_POV, null)

        //Act
        fragmentScenario.onFragment { fragment ->
            val view = View.inflate(fragment.requireContext(), R.layout.fragment_details, null)

            fragment.onViewCreated(view, null)

            //Assert
            assertNotNull(view)
        }
    }

    //MyBottomSheetDialogFragment
    @Test
    fun testMapDaysToNumber() {
        //Arrange
        val fragment =
            DetailsFragment.MyBottomSheetDialogFragment() // Crie uma instância da sua classe

        //Assert
        assertEquals(1, fragment.mapDaysToNumber("1 Days"))
        assertEquals(3, fragment.mapDaysToNumber("3 Days"))
        assertEquals(7, fragment.mapDaysToNumber("7 Days"))
        assertEquals(30, fragment.mapDaysToNumber("30 Days"))
        assertEquals(Long.MAX_VALUE, fragment.mapDaysToNumber("Unlimited"))
        assertEquals(0, fragment.mapDaysToNumber("Invalid Value"))
    }
    @Test
    fun testCheckGroupsForAutoDeletion() {
        //Arrange
        val currentDate = Date()

        //Act
        val groups = listOf(
            createGroup(currentDate, "3 Days"),
            createGroup(currentDate, "7 Days"),
            createGroup(currentDate, "Unlimited"),
            createGroup(currentDate, "1 Days"),
            createGroup(currentDate, "30 Days")
        )
        val deletedGroups = checkGroupsForAutoDeletion(currentDate, groups)

        //Assert
        assertNotSame(groups.size, deletedGroups.size)
    }

    private fun createGroup(currentDate: Date, daysString: String): Group {
        val timestamp = Date(currentDate.time - TimeUnit.DAYS.toMillis(2))
        return Group(timestamp, daysString)
    }

    private fun checkGroupsForAutoDeletion(currentDate: Date, groups: List<Group>): List<Group> {
        val groupsToDelete = mutableListOf<Group>()
        for (group in groups) {
            val timestamp = group.timestamp
            val selectDays = group.selectDays

            if (timestamp != null && selectDays != null) {
                val creationDate = Date(timestamp.time)
                val daysDifference = TimeUnit.MILLISECONDS.toDays(currentDate.time - creationDate.time)
                val numberOfDays = mapDaysToNumber(selectDays)

                if (daysDifference >= numberOfDays) {
                    groupsToDelete.add(group)
                }
            }
        }
        return groupsToDelete
    }

    private fun mapDaysToNumber(daysString: String): Long {
        when (daysString) {
            "1 Days" -> return 1
            "3 Days" -> return 3
            "7 Days" -> return 7
            "30 Days" -> return 30
            "Unlimited" -> return Long.MAX_VALUE
            else -> return 0
        }
    }

    data class Group(val timestamp: Date?, val selectDays: String)
}


