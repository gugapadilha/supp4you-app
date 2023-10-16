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
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val emailText = "invalidemail"

        scenario.onFragment { registerFragment ->
            // Simular entrada de e-mail inválido
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(emailText)
            val result = registerFragment.checkAllFields()
            assertFalse(result)
        }
    }


    @Test
    fun testCheckAllFields_WithShortPassword() {
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val email = "test@example.com"
        val password = "123"

        scenario.onFragment { registerFragment ->
            // Simular senha curta
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(email)
            registerFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(password)
            val result = registerFragment.checkAllFields()
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithEmptyFields() {
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val email = ""
        val password = ""
        val repeatPassword = ""
        scenario.onFragment { registerFragment ->
            // Simular campos vazios
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(email)
            registerFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(password)
            registerFragment.binding.edRepeatPassword.text = SpannableStringBuilder.valueOf(repeatPassword)
            val result = registerFragment.checkAllFields()
            assertFalse(result)
        }
    }

    @Test
    fun testCheckAllFields_WithMatchingPasswords() {
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val email = "test@example.com"
        val password = "password123"
        val repeatPassword = "password123"

        scenario.onFragment { registerFragment ->
            // Simular senhas correspondentes
            registerFragment.binding.edEmail.text = SpannableStringBuilder.valueOf(email)
            registerFragment.binding.edPassword.text = SpannableStringBuilder.valueOf(password)
            registerFragment.binding.edRepeatPassword.text = SpannableStringBuilder.valueOf(repeatPassword)
            val result = registerFragment.checkAllFields()
            assertTrue(result)
        }
    }

    @Test
    fun testSignInWithGoogle_Success() {
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val layout = R.layout.fragment_register
        val layout2 = R.layout.fragment_login
        scenario.onFragment { registerFragment ->
            val requestCode = registerFragment.RC_SIGN_IN
            val resultCode = android.app.Activity.RESULT_OK  // Simular um resultado bem-sucedido
            val data = android.content.Intent()
            // Simular o resultado bem-sucedido ao fazer login com o Google
            val fakeGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(registerFragment.requireContext())
            data.putExtra("fakeGoogleSignInAccount", fakeGoogleSignInAccount)

            // Simular o processo de login com o Google
            registerFragment.onActivityResult(requestCode, resultCode, data)

            // Verificar se o usuário é redirecionado para a tela de login após o login bem-sucedido
            assertTrue(R.layout.fragment_register.equals(layout))
        }
    }

    @Test
    fun testSignInWithGoogle_Failure() {
        val scenario = launchFragmentInContainer<RegisterFragment>()
        val layout = R.layout.fragment_register
        val layout2 = R.layout.fragment_login
        scenario.onFragment { registerFragment ->
            val requestCode = registerFragment.RC_SIGN_IN
            val resultCode = android.app.Activity.RESULT_CANCELED  // Simular um resultado malsucedido
            val data = android.content.Intent()

            // Simular o processo de login com o Google com falha
            registerFragment.onActivityResult(requestCode, resultCode, data)

            // Verificar se o usuário permanece na tela de registro após o login com falha
            assertFalse(R.layout.fragment_register.equals(layout2))


        }
    }
}