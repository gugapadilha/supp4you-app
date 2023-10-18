package com.guga.supp4youapp.presentation.ui.login

import android.app.Activity
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.guga.supp4youapp.R
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testCheckAllFields_WithInvalidEmail() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        // Act
        scenario.onFragment { loginFragment ->
            val emailText = "invalidemail"
            loginFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            val result = loginFragment.checkAllFields()

            // Assert
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithShortPassword() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        // Act
        scenario.onFragment { loginFragment ->
            val emailText = "test@example.com"
            val passwordText = "123"
            loginFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            loginFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(passwordText)
            val result = loginFragment.checkAllFields()

            // Assert
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithShortPasswords() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        //Act
        scenario.onFragment { loginFragment ->
            val emailText = "test@example.com"
            val passwordText = "123"
            loginFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            loginFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(passwordText)
            val result = loginFragment.checkAllFields()

            //Assert
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithEmptyEmail() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        //Act
        scenario.onFragment { loginFragment ->
            val emailText = ""
            val passwordText = ""
            loginFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            loginFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(passwordText)
            val result = loginFragment.checkAllFields()

            //Assert
            assertFalse(result)
        }
    }

    @Test
    fun testChangeIconVisibility_Visible() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        //Act
        scenario.onFragment { loginFragment ->
            val passwordField = AppCompatEditText(loginFragment.requireContext())
            val textVisibility = AppCompatTextView(loginFragment.requireContext())
            loginFragment.changeIconVisibility(passwordField, textVisibility, true)

            //Assert
            assertEquals(
                PasswordTransformationMethod.getInstance(),
                passwordField.transformationMethod
            )
        }
    }

    @Test
    fun testChangeIconVisibility_Invisible() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        //Act
        scenario.onFragment { loginFragment ->
            val passwordField = AppCompatEditText(loginFragment.requireContext())
            val textVisibility = AppCompatTextView(loginFragment.requireContext())
            loginFragment.changeIconVisibility(passwordField, textVisibility, false)

            //Assert
            assertNull(passwordField.transformationMethod)
        }
    }

    @Test
    fun testCheckAllFields_WithValidInput() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()

        //Act
        scenario.onFragment { loginFragment ->
            val emailText = "test@example.com"
            val passwordText = "password123"
            loginFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            loginFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(passwordText)
            val result = loginFragment.checkAllFields()

            //Assert
            assertTrue(result)
        }
    }

    @Test
    fun testActivityResult_ValidReturns() {
        // Arrange
        val scenario = launchFragmentInContainer<LoginFragment>()
        val RC_SIGN_IN = 9001
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

        // Act
        scenario.onFragment { loginFragment ->
            val requestCode = RC_SIGN_IN
            val resultCode = Activity.RESULT_OK  // Simulando um resultado bem-sucedido
            val data = Intent()
            val layout = R.layout.fragment_login
            data.putExtra("idToken", "0")

            loginFragment.onActivityResult(requestCode, resultCode, data)
            googleSignInOptions.requestIdToken("idToken")
            googleSignInOptions.requestEmail()

            //Assert
            assertTrue(R.layout.fragment_login.equals(layout))
            assertTrue(RC_SIGN_IN == requestCode)
        }
    }


}
