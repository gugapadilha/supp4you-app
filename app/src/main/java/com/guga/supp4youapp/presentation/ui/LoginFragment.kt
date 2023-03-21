package com.guga.supp4youapp.presentation.ui
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.data.DataSourceLogin
import com.guga.supp4youapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)

        binding.tvCreatenow.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvLogin.setOnClickListener {
            val username = binding.edEmail.text.toString()
            val password = binding.tvPassword.text.toString()

            val loginDataSet = DataSourceLogin.createDataSetLogin()

            var isLoginSuccessful = false
            for (loginRequest in loginDataSet) {
                if (loginRequest.value?.email == username && loginRequest.value?.password == password) {
                    isLoginSuccessful = true
                    break
                }
            }

            if (isLoginSuccessful) {
                findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)
            } else {
                Toast.makeText(requireContext(), "Invalid Credencials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}