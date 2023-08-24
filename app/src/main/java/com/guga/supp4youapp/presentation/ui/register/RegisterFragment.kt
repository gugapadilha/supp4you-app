package com.guga.supp4youapp.presentation.ui.register

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
import com.guga.supp4youapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        auth = Firebase.auth

        binding.tvLogin.setOnClickListener {
            val username = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            if (checkAllFields()) {
                auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        auth.signOut()
                        Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                    }else {
                        Log.e("error: ", it.exception.toString())
                    }
                }
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        registerViewModel.visiblePassword.observe(viewLifecycleOwner) { visible ->
            changePasswordVisibility(
                binding.edPassword,
                binding.visibilityOff,
                visible
            )
        }

        registerViewModel.senhaRepeticaoVisivel.observe(viewLifecycleOwner) { visible ->
            changePasswordVisibility(
                binding.edRepeatPassword,
                binding.visibility,
                visible
            )
        }

        binding.visibilityOff.setOnClickListener {
            registerViewModel.changeVisibilityPassowrd()
        }

        binding.visibility.setOnClickListener {
            registerViewModel.changeVisibilityPasswordRep()
        }
    }
    private fun changePasswordVisibility(
        passwordField: AppCompatEditText,
        textVisibility: AppCompatTextView,
        visible: Boolean
    ) {
        if (visible) {
            passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
            textVisibility.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.baseline_visibility_off_24,
                0
            )
        } else {
            passwordField.transformationMethod = null
            textVisibility.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.baseline_visibility_24,
                0
            )
        }
        passwordField.setSelection(passwordField.text!!.length)
    }

    private fun checkAllFields() : Boolean {
        val email = binding.edEmail.text.toString()

        if (binding.edEmail.text.toString() == ""){
            Toast.makeText(requireContext(), "Email is a required field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(requireContext(), "Check email format", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edPassword.text.toString() == ""){
            Toast.makeText(requireContext(), "Password is a required field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edPassword.length() <= 7) {
            Toast.makeText(requireContext(), "Password should at least 8 characters long", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edRepeatPassword.toString() == ""){
            Toast.makeText(requireContext(), "Repeat Password is a required field", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edPassword.text.toString() != binding.edRepeatPassword.text.toString()){
            Toast.makeText(requireContext(), "Password do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
