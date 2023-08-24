package com.guga.supp4youapp.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentLoginBinding
import com.guga.supp4youapp.presentation.ui.camera.CameraActivity

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        togglePasswordVisibility(binding.edPassword, binding.visibility)
        auth = Firebase.auth

        binding.tvCreatenow.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            if (checkAllFields()){
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(requireContext(), "Successfully sign in", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)

                    }else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
        }

        binding.visibility.setOnClickListener {
            togglePasswordVisibility(binding.edPassword, binding.visibility)
        }

    }

    private fun checkAllFields() : Boolean {
        val email = binding.edEmail.text.toString()

        if (binding.edEmail.text.toString() == "") {
            Toast.makeText(requireContext(), "Email is a required field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Check email format", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edPassword.text.toString() == "") {
            Toast.makeText(requireContext(), "Password is a required field", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (binding.edPassword.length() <= 6) {
            Toast.makeText(
                requireContext(),
                "Password should at least 8 characters long",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
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