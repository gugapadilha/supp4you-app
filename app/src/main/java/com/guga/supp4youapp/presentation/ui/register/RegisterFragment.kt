package com.guga.supp4youapp.presentation.ui.register

import android.content.ContentValues.TAG
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    val RC_SIGN_IN = 9001

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        auth = Firebase.auth
        binding.progressBar.visibility = View.GONE
        binding.tvLogin.setOnClickListener {
            val username = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            if (checkAllFields()) {
                binding.progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        auth.signOut()
                        Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("error: ", it.exception.toString())
                    }

                    binding.progressBar.visibility = View.GONE

                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        }

        registerViewModel.visiblePassword.observe(viewLifecycleOwner) { visible ->
            changeIconVisibility(
                binding.edPassword,
                binding.visibilityOff,
                visible
            )
        }

        registerViewModel.passwordRepetition.observe(viewLifecycleOwner) { visible ->
            changeIconVisibility(
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

        binding.clBtnGoogle.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            signInWithGoogle()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.GONE

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val idToken = account.idToken
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Login with Google successful", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            } else {
                                Toast.makeText(requireContext(), "Failed to login with Google", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun changeIconVisibility(
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

    fun checkAllFields() : Boolean {
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
