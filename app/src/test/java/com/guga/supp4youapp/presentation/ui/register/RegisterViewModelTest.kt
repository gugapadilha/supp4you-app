package com.guga.supp4youapp.presentation.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel()
    }

    @Test
    fun testChangeVisibilityPassword() {
        // Inicialmente, o valor de visiblePassword deve ser verdadeiro
        Assert.assertTrue(viewModel.visiblePassword.value == true)

        // Chame o método para alterar a visibilidade da senha
        viewModel.changeVisibilityPassowrd()

        // Agora, o valor de visiblePassword deve ser falso
        Assert.assertTrue(viewModel.visiblePassword.value == false)
    }

    @Test
    fun testChangeVisibilityPasswordRep() {
        // Inicialmente, o valor de passwordRepetition deve ser verdadeiro
        Assert.assertTrue(viewModel.passwordRepetition.value == true)

        // Chame o método para alterar a visibilidade da repetição de senha
        viewModel.changeVisibilityPasswordRep()

        // Agora, o valor de passwordRepetition deve ser falso
        Assert.assertTrue(viewModel.passwordRepetition.value == false)
    }
}
