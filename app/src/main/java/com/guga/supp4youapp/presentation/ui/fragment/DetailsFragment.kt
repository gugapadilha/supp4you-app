package com.guga.supp4youapp.presentation.ui.fragment

import android.content.Intent
import android.net.Uri
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
    private var isSignOutDialogShowing = false
    private val personCollectionRef = Firebase.firestore.collection("photos")
    private var personName: String = ""
    private var takenPhotoUri: Uri? = null // Adicione isso



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
            val name = binding.textView.text.toString()
            if (name.isNotEmpty()) {
                personName = name

                // Gere um nome exclusivo para a foto usando um timestamp
                val timestamp = System.currentTimeMillis()
                val photoName = "$personName"

                // Capture a foto e obtenha a URI
                takePhotoAndGetUri(photoName)
            } else {
                Toast.makeText(requireContext(), "Enter a name first", Toast.LENGTH_SHORT).show()
            }
        }


        binding.tvCreatespace.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_accessFragment)
            val name = binding.textView.text.toString()
            personName = name

        }

        binding.back.setOnClickListener {
            showSignOutConfirmationDialog()
        }

        binding.backIcon.setOnClickListener {
            showSignOutConfirmationDialog()
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

    private fun takePhotoAndGetUri(photoName: String) {
        // ... O código para capturar a foto

        // Configurar a URI da foto capturada na instância da MyBottomSheetDialogFragment
        val bottomSheetFragment = MyBottomSheetDialogFragment()
        val args = Bundle()
        args.putString("personName", personName)
        args.putString("photoName", photoName)
        args.putParcelable("takenPhotoUri", takenPhotoUri) // Configurar a URI aqui
        bottomSheetFragment.arguments = args

        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {
        private var enteredToken: String? = null
        private var personName: String? = null
        private var photoName: String? = null
        private var takenPhotoUri: Uri? = null // Adicione isso


        // Método para configurar os argumentos
        fun setArguments(personName: String, photoName: String, takenPhotoUri: Uri) {
            this.personName = personName
            this.photoName = photoName
            this.takenPhotoUri = takenPhotoUri // Configure o takenPhotoUri
        }

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

                if (code.isNotEmpty()) {
                    enteredToken = code

                    // Acessar os argumentos configurados
                    val personName = arguments?.getString("personName")
                    val photoName = arguments?.getString("photoName")

                    if (personName != null && photoName != null) {
                        // Use personName e photoName conforme necessário

                        val groupDocumentRef = Firebase.firestore.collection("create").document(code)
                        groupDocumentRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    // Use o photoName como um identificador exclusivo no Firestore
                                    val firestore = Firebase.firestore
                                    val photoData = hashMapOf(
                                        "photoUri" to takenPhotoUri.toString(),
                                        "groupId" to enteredToken,
                                        "personName" to personName,
                                        "photoName" to photoName // Use o photoName como um identificador exclusivo
                                    )

                                    val intent = Intent(requireContext(), CameraActivity::class.java)
                                    intent.putExtra("groupId", enteredToken)
                                    intent.putExtra("personName", personName)
                                    intent.putExtra("photoName", photoName)
                                    startActivity(intent)
                                    dismiss()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Code does not exist",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error when validating the code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "You should insert a generated code", Toast.LENGTH_SHORT).show()
                }
            }

            return view
        }
    }


}