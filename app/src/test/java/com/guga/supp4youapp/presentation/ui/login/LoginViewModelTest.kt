package com.guga.supp4youapp.presentation.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel()
    }

    @Test
    fun testInitialVisibility() {
        // Assert that the initial visibility value is true
        assertTrue(viewModel.visiblePassword.value == true)
    }

    @Test
    fun testToggleVisibility() {
        // Toggle visibility and assert the new value
        viewModel.changeVisibilityPassowrd()
        assertTrue(viewModel.visiblePassword.value == false)

        // Toggle visibility again and assert the new value
        viewModel.changeVisibilityPassowrd()
        assertTrue(viewModel.visiblePassword.value == true)
    }
}
