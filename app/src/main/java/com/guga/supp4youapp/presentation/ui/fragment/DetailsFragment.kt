package com.guga.supp4youapp.presentation.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentDetailsBinding
import com.guga.supp4youapp.presentation.ui.camera.CameraActivity
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

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
        // Recupere o nome do usuário dos SharedPreferences

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        // Recupere o nome do usuário dos SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val savedName = sharedPreferences.getString("personName", "")

    // Se o nome do usuário foi salvo anteriormente, preencha o campo de texto
        if (savedName != null && savedName.isNotEmpty()) {
            binding.textView.text = Editable.Factory.getInstance().newEditable(savedName)
        }

        binding.tvLoginspace.setOnClickListener {
            val name = binding.textView.text.toString()
            if (name.isNotEmpty()) {
                personName = name

                // Salve o nome do usuário nos SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("personName", personName)
                editor.apply()

                takePhotoAndGetUri(personName)
            } else {
                Toast.makeText(requireContext(), "Enter a name first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvCreatespace.setOnClickListener {
            val name = binding.textView.text.toString()
            personName = name

            // Salve o nome do usuário nos SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("personName", personName)
            editor.apply()

            // Crie um Bundle para passar o nome para a AccessFragment
            val bundle = Bundle()
            bundle.putString("personName", personName)

            findNavController().navigate(R.id.action_detailsFragment_to_accessFragment, bundle)
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
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString("personName", "")

        // Se o nome do usuário foi salvo anteriormente, preencha o campo de texto
        if (savedName != null && savedName.isNotEmpty()) {
            binding.textView.text = Editable.Factory.getInstance().newEditable(savedName)
        }
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
                    val personName = arguments?.getString("personName")
                    val photoName = arguments?.getString("photoName")
                    val groupDocumentRef = Firebase.firestore.collection("create").document(code)
                    groupDocumentRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                // O documento da sala foi encontrado
                                val selectBeginTimeFromFirestore = document.getString("selectBeginTime")
                                val selectEndTimeFromFirestore = document.getString("selectEndTime")
                                val selectDaysFromFirestore = document.getString("selectDays")

                                // Agora você tem os valores de selectBeginTime e selectEndTime do Firestore
                                // Você pode passá-los para CameraActivity
                                val intent = Intent(requireContext(), CameraActivity::class.java)
                                intent.putExtra("groupId", enteredToken)
                                intent.putExtra("personName", personName)
                                intent.putExtra("photoName", photoName)
                                intent.putExtra("selectBeginTime", selectBeginTimeFromFirestore)
                                intent.putExtra("selectEndTime", selectEndTimeFromFirestore)
                                intent.putExtra("selectDays", selectDaysFromFirestore)

                                // Após verificar que o código do grupo existe e obter o ID do grupo
                                val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                                val groupId = enteredToken

                                // Verificar se há um URI de foto associado ao grupo nos SharedPreferences
                                val savedPhotoUriString = sharedPreferences.getString(groupId, null)

                                if (savedPhotoUriString != null) {
                                    val savedPhotoUri = Uri.parse(savedPhotoUriString)
                                    // Passar o URI da foto para a CameraActivity
                                    intent.putExtra("photoUri", savedPhotoUri.toString())
                                }

                                startActivity(intent)
                                dismiss()

                                startActivity(intent)
                                dismiss()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Code does not exist anymore",
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
                    Toast.makeText(requireContext(), "You should insert a generated code", Toast.LENGTH_SHORT).show()
                }
            }
            checkGroupsForAutoDeletion()
            return view
        }
        private fun checkGroupsForAutoDeletion() {
            val db = FirebaseFirestore.getInstance()
            val collectionReference = db.collection("create")

            // Data atual
            val currentDate = Date()

            collectionReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val timestamp = document.getLong("timesTamp")
                        val selectDays = document.getString("selectDays")

                        if (timestamp != null && selectDays != null) {
                            // Calcular a data de criação do grupo
                            val creationDate = Date(timestamp)

                            // Calcular a diferença em dias entre a data atual e a data de criação
                            val daysDifference = TimeUnit.MILLISECONDS.toDays(currentDate.time - creationDate.time)

                            // Obter o número de dias a partir da função mapDaysToNumber
                            val numberOfDays = mapDaysToNumber(selectDays)

                            // Verificar se a diferença é maior ou igual ao número de dias em selectDays
                            if (daysDifference >= numberOfDays) {
                                // Excluir o grupo
                                val documentReference = collectionReference.document(document.id)
                                documentReference.delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Group doesnt exist anymore", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "Error deleting group: $e", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error while searching group: ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun mapDaysToNumber(daysString: String): Long {
            when (daysString) {
                "1 Days" -> return 1
                "3 Days" -> return 3
                "7 Days" -> return 7
                "30 Days" -> return 30
                "Unlimited" -> return Long.MAX_VALUE
                else -> return 0
            }
        }
    }


}