package com.guga.supp4youapp.presentation.ui.register

import android.text.SpannableStringBuilder
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.fragment.findNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseApp
import com.guga.supp4youapp.R
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testCheckAllFields_WithInvalidEmail() {
        //Arrange
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val emailText = "invalidemail"

        //Act
        scenario.onFragment { registerFragment ->
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            val result = registerFragment.checkAllFields()
            //Assert
            assertFalse(result)
        }
    }


    @Test
    fun testCheckAllFields_WithShortPassword() {
        //Arrange
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val email = "test@example.com"
        val password = "123"

        //Act
        scenario.onFragment { registerFragment ->
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(email)
            registerFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(password)
            val result = registerFragment.checkAllFields()
            //Assert
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithEmptyFields() {
        //Ararnge
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val email = ""
        val password = ""
        val repeatPassword = ""

        //Act
        scenario.onFragment { registerFragment ->
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(email)
            registerFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(password)
            registerFragment.binding.edRepeatPassword.text = SpannableStringBuilder.valueOf(repeatPassword)
            val result = registerFragment.checkAllFields()

            //Assert
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithMatchingPasswords() {
        //Arrange
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val email = "test@example.com"
        val password = "password123"
        val repeatPassword = "password123"

        //Act
        scenario.onFragment { registerFragment ->
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(email)
            registerFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(password)
            registerFragment.binding.edRepeatPassword.text = SpannableStringBuilder.valueOf(repeatPassword)
            val result = registerFragment.checkAllFields()

            //Assert
            assertTrue(result)
        }
    }

    @Test
    fun testSignInWithGoogle_Success() {
        //Arrange
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val layout = R.layout.fragment_register
        val layout2 = R.layout.fragment_login

        //Act
        scenario.onFragment { registerFragment ->
            val requestCode = registerFragment.RC_SIGN_IN
            val resultCode = android.app.Activity.RESULT_OK
            val data = android.content.Intent()
            val fakeGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(registerFragment.requireContext())
            data.putExtra("fakeGoogleSignInAccount", fakeGoogleSignInAccount)

            registerFragment.onActivityResult(requestCode, resultCode, data)

            //Assert
            assertTrue(R.layout.fragment_register.equals(layout))
        }
    }

    @Test
    fun testSignInWithGoogle_Failure() {
        //Arrange
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val layout = R.layout.fragment_register
        val layout2 = R.layout.fragment_login

        //Act
        scenario.onFragment { registerFragment ->
            val requestCode = registerFragment.RC_SIGN_IN
            val resultCode = android.app.Activity.RESULT_CANCELED
            val data = android.content.Intent()


            registerFragment.onActivityResult(requestCode, resultCode, data)

            //Assert
            assertFalse(R.layout.fragment_register.equals(layout2))

        }
    }
}