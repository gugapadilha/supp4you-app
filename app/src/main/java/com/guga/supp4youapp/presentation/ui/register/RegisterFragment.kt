package com.guga.supp4youapp.presentation.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        binding.tvLogin.setOnClickListener {
            val username = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            val rePassword = binding.edRepeatPassword.text.toString()

            val isLoginSuccessful = registerViewModel.validateRegister(username, password, rePassword)

            if (isLoginSuccessful) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "Invalid Credencials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
