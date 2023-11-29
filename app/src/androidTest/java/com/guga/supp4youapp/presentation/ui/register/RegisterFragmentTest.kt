package com.guga.supp4youapp.presentation.ui.register

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getAuthInstance(): FirebaseAuth {
        return auth
    }
    @Test
    fun testLoginWithValidCredentials() {
        val fragment = RegisterFragment()
        val logged = true

        // Set valid email and password
        fragment.binding.edEmail.setText("johndoe@example.com")
        fragment.binding.edPassword.setText("password123")
        fragment.binding.edRepeatPassword.setText("password123")

        // Trigger login button click
        fragment.binding.tvLogin.performClick()

        // Verify that the user was successfully logged in
        assertThat(fragment.checkAllFields().toString(), logged)
    }

}