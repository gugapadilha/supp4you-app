package com.guga.supp4youapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.data.DataSourceLogin2
import com.guga.supp4youapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val userList = DataSourceLogin2.createDataSetLogin()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)

        binding.tvCreatenow.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvLogin.setOnClickListener {
            logInValidate()
        }
    }

    private fun logInValidate() {
        for (userLiveData in userList) {
            val user = userLiveData.email
            val user2 = userLiveData.password
            if (user.equals(binding.edEmail) && user2.equals(binding.tvPassword)) {
                findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)
            } else {
                Toast.makeText(requireContext(), "Invalid Credencials.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}