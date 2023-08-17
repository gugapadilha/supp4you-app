package com.guga.supp4youapp.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        binding.tvCreatenow.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvLogin.setOnClickListener {
                val intent = Intent(requireContext(), CameraActivity::class.java)
                startActivity(intent)

//            val username = binding.edEmail.text.toString()
//            val password = binding.edPassword.text.toString()
//
//            val isLoginSuccessful = loginViewModel.validateLogin(username, password)
//
//            if (isLoginSuccessful) {
//                findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)
//            } else {
//                Toast.makeText(requireContext(), "Invalid Credencials", Toast.LENGTH_SHORT).show()
//            }
        }

    }
}