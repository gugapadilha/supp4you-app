package com.guga.supp4youapp.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.data.remote.database.Person
import com.guga.supp4youapp.databinding.FragmentDetailsBinding
import com.guga.supp4youapp.presentation.ui.camera.CameraActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var isSignOutDialogShowing = false // Variável para controlar o estado do diálogo
    private val personCollectionRef = Firebase.firestore.collection("persons")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.tvLoginspace.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetDialogFragment()
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            val name = binding.textView.text.toString()
            val person = Person(name, "")
            savePerson(person)

        }

        binding.tvCreatespace.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_accessFragment)
            val name = binding.textView.text.toString()
            val person = Person(name, "")
            savePerson(person)

        }

        binding.back.setOnClickListener {
            showSignOutConfirmationDialog()
        }

        binding.backIcon.setOnClickListener {
            showSignOutConfirmationDialog()
        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val docRef = personCollectionRef.add(person).await()
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Successfully saved data", Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString("spaceId", docRef.id)
                val detailsFragment = DetailsFragment()
                detailsFragment.arguments = bundle
            }

        } catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignOutConfirmationDialog() {
        isSignOutDialogShowing = true

        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_sign_out, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogMessage.text = "Are you sure you want to sign out?"

        btnYes.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_detailsFragment_to_loginFragment)
            dialog.dismiss()
            Toast.makeText(requireContext(), "Sign out success", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            isSignOutDialogShowing = false
        }

        dialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        isSignOutDialogShowing = false

        requireActivity().onBackPressedDispatcher.addCallback(this) {
        }
    }

    class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {
        private var enteredToken: String? = null

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)

            val enterSpaceButton = view.findViewById<Button>(R.id.tv_enter_space)
            enterSpaceButton.setOnClickListener {
                val codeEditText = view.findViewById<EditText>(R.id.ed_token)
                val code = codeEditText.text.toString()

                // Verificar se o campo de código está vazio
                if (code.isBlank()) {
                    // Exibir uma mensagem de erro caso o campo esteja vazio
                    Toast.makeText(requireContext(), "You have to write a valid code!", Toast.LENGTH_SHORT).show()
                } else {
                    // Implementar a verificação do código de acesso aqui

                    val groupDocumentRef = Firebase.firestore.collection("create").document(code)
                    groupDocumentRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                // Código de acesso é válido, redirecionar para a tela de conversa
                                val intent = Intent(requireContext(), CameraActivity::class.java)
                                intent.putExtra("groupId", enteredToken)
                                startActivity(intent)
                                dismiss()
                            } else {
                                // Código de acesso inválido, exibir Toast
                                Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Lidar com erros
                            Toast.makeText(requireContext(), "Code might be wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            return view
        }
    }

}