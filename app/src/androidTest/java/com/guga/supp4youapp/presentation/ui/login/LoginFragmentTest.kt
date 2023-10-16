package com.guga.supp4youapp.presentation.ui.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guga.supp4youapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @Test
    fun testGoogleSignIn() {
        val scenario = launchFragmentInContainer<LoginFragment>()

        // Espere um tempo suficiente para que a IU seja carregada e o botão do Google Sign-In seja visível
        Thread.sleep(2000)

        // Simule o clique no botão de login com o Google com base no ID 'cl_btn_google'
        onView(withId(R.id.cl_btn_google)).perform(click())

        // Aguarde um tempo suficiente para o processo de sign-in do Google
        Thread.sleep(5000)

        onView(withId(R.layout.fragment_details)).check(matches(isDisplayed()))

        // Espere algum tempo para observar a IU após o sign-in do Google (substitua com a verificação real)
        Thread.sleep(2000)
    }

}
