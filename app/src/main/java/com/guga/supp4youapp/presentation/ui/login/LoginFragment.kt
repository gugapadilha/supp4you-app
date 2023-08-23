package com.guga.supp4youapp.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentLoginBinding
import com.guga.supp4youapp.presentation.ui.camera.CameraActivity

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        togglePasswordVisibility(binding.edPassword, binding.visibility)

        binding.tvCreatenow.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)
        }

        binding.visibility.setOnClickListener {
            togglePasswordVisibility(binding.edPassword, binding.visibility)
        }

    }

    private fun togglePasswordVisibility(passwordEditText: AppCompatEditText, visibilityTextView: AppCompatTextView) {
        if (passwordEditText.transformationMethod == null) {
            // A senha está visível, então tornamos oculta
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            visibilityTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0)
        } else {
            // A senha está oculta, então tornamos visível
            passwordEditText.transformationMethod = null
            visibilityTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0)
        }
        // Move o cursor para o final do texto
        passwordEditText.setSelection(passwordEditText.text!!.length)
    }
}