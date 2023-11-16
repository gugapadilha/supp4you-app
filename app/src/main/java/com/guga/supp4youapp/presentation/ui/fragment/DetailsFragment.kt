package com.guga.supp4youapp.presentation.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import com.guga.supp4youapp.presentation.ui.gallery.GalleryActivity
import com.guga.supp4youapp.presentation.ui.group.AllGroupsActivity
import com.guga.supp4youapp.presentation.ui.group.GroupManager
import com.guga.supp4youapp.presentation.ui.group.GroupModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DetailsFragment : Fragment(R.layout.fragment_details) {

    var _binding: FragmentDetailsBinding? = null
    val binding get() = _binding!!
    lateinit var auth: FirebaseAuth
    var isSignOutDialogShowing = false
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

        binding.tvSeespace.setOnClickListener {
            GroupManager.loadEnteredGroups(requireContext())

            val intent = Intent(requireContext(), AllGroupsActivity::class.java)
            intent.putExtra("personName", personName)
            startActivity(intent)
        }


        binding.back.setOnClickListener {
            showSignOutConfirmationDialog()
        }

        binding.backIcon.setOnClickListener {
            showSignOutConfirmationDialog()
        }
    }


    fun showSignOutConfirmationDialog() {
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

    fun takePhotoAndGetUri(photoName: String) {

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
        private var lastMarkedBeginTime: Long? = 0L

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
                                val selectBeginTimeFromFirestore = document.getString("selectBeginTime")
                                val selectEndTimeFromFirestore = document.getString("selectEndTime")
                                val selectDaysFromFirestore = document.getString("selectDays")
                                val groupName = document.getString("groupName")
                                val createTimestamp = document.getLong("timesTamp")

                                // Verifique se o dia atual já terminou com base no endTime do Firestore
                                if (isCurrentTimeAfterEndTime(selectEndTimeFromFirestore)) {
                                    // O dia atual terminou, então remova o URI da foto dos SharedPreferences
                                    clearPhotoUriFromSharedPreferences(code)

                                    // Além disso, você pode excluir qualquer URI da foto no Firestore se necessário
                                    // clearPhotoUriFromFirestore(code)
                                }
                                if (selectBeginTimeFromFirestore != null) {
                                    checkAndMarkPhotosAsDeleted(code, selectBeginTimeFromFirestore, personName,
                                        createTimestamp!!
                                    )
                                }

                                // Continuar com a lógica de verificar se há um URI de foto nos SharedPreferences
                                val sharedPreferences = requireContext().getSharedPreferences(
                                    "MyPreferences",
                                    Context.MODE_PRIVATE
                                )
                                val photoUriString = sharedPreferences.getString(code, null)

                                // Verifique se a foto do usuário já existe no grupo
                                val firestore = Firebase.firestore
                                firestore.collection("photos")
                                    .whereEqualTo("groupId", code)
                                    .whereEqualTo("personName", personName)
                                    .get()
                                    .addOnSuccessListener { userPhotosQuerySnapshot ->
                                        if (!userPhotosQuerySnapshot.isEmpty) {
                                            GroupManager.addGroup(GroupModel(groupName = groupName.toString(), groupCode = enteredToken!!.toInt(), beginTime = selectBeginTimeFromFirestore!!, endTime = selectEndTimeFromFirestore!!))
                                            GroupManager.saveEnteredGroups(requireContext())
                                            // O usuário atual já tirou uma foto no grupo, redirecione para a GalleryActivity
                                            val intent = Intent(
                                                requireContext(),
                                                GalleryActivity::class.java
                                            )
                                            intent.putExtra("groupId", code)
                                            intent.putExtra("personName", personName)
                                            intent.putExtra("photoName", photoName)
                                            startActivity(intent)
                                        } else {
                                            GroupManager.addGroup(GroupModel(groupName = groupName.toString(), groupCode = enteredToken!!.toInt(), beginTime = selectBeginTimeFromFirestore!!, endTime = selectEndTimeFromFirestore!!))
                                            GroupManager.saveEnteredGroups(requireContext())
                                            // O usuário atual não tirou uma foto anteriormente, redirecione-o para a CameraActivity
                                            val intent =
                                                Intent(requireContext(), CameraActivity::class.java)
                                            intent.putExtra("groupId", enteredToken)
                                            intent.putExtra("personName", personName)
                                            intent.putExtra("photoName", photoName)
                                            intent.putExtra(
                                                "selectBeginTime",
                                                selectBeginTimeFromFirestore
                                            )
                                            intent.putExtra(
                                                "selectEndTime",
                                                selectEndTimeFromFirestore
                                            )
                                            intent.putExtra("selectDays", selectDaysFromFirestore)
                                            startActivity(intent)
                                        }
                                        dismiss()
                                    }
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
                    Toast.makeText(
                        requireContext(),
                        "You should insert a generated code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            checkGroupsForAutoDeletion()
            return view
        }

        fun checkGroupsForAutoDeletion() {
            val db = FirebaseFirestore.getInstance()
            val collectionReference = db.collection("create")

            val currentTime = Calendar.getInstance()
            val currentDate = Date()

            collectionReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    1
                    for (document in task.result) {
                        val timestamp = document.getLong("timesTamp")
                        val selectBeginTime = document.getString("selectBeginTime")
                        val selectEndTime = document.getString("selectEndTime")
                        val selectDays = document.getString("selectDays")
                        val groupId = document.id

                        if (timestamp != null && selectDays != null) {
                            val creationDate = Date(timestamp)
                            val daysDifference =
                                TimeUnit.MILLISECONDS.toDays(currentDate.time - creationDate.time)
                            val numberOfDays = mapDaysToNumber(selectDays)
                            val expirationDate = Calendar.getInstance()
                            expirationDate.time = creationDate
                            expirationDate.add(Calendar.DAY_OF_YEAR, numberOfDays.toInt())

                            if (daysDifference >= numberOfDays) {
                                // Se a data de expiração foi atingida, exclua o documento e marque as fotos como ocultas
                                val documentReference = collectionReference.document(groupId)
                                documentReference.delete()
                                    .addOnSuccessListener {
                                        // Documento excluído com sucesso
                                        markPhotosAsHidden(groupId)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Error deleting document: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else if (isCurrentTimeAfterBeginTime(selectBeginTime)) {
                                // Se a data atual for posterior ao próximo selectBeginTime, atualize isDeleted
                                val documentReference = collectionReference.document(groupId)
                                documentReference.update("isDeleted", true)
                                    .addOnSuccessListener {
                                        // Atualização do campo isDeleted para true com sucesso
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Error updating isDeleted: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error while searching group: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        private fun isAfterLastMarkedBeginTime(beginTime: String?, creationTimestamp: Long): Boolean {
            if (beginTime != null) {
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime = Calendar.getInstance().time
                val beginTimeDate = formatter.parse(beginTime)

                val creationTime = Calendar.getInstance()
                creationTime.timeInMillis = creationTimestamp

                return currentTime.after(beginTimeDate) && currentTime.after(creationTime.time)
            }
            return false
        }

        fun checkAndMarkPhotosAsDeleted(groupId: String, selectBeginTime: String, personName: String?, createTimestamp: Long) {
            val currentTime = Calendar.getInstance()

            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val beginTimeDate = Calendar.getInstance().apply {
                time = Date()
                val beginTimeParts = selectBeginTime.split(":")
                if (beginTimeParts.size == 2) {
                    set(Calendar.HOUR_OF_DAY, beginTimeParts[0].toInt())
                    set(Calendar.MINUTE, beginTimeParts[1].toInt())
                    set(Calendar.SECOND, 0)
                }
            }.time

            // Obtenha o número de dias restantes com base em `selectDays`
            val remainingDays = getRemainingDaysFromSelectDays()

            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_YEAR, remainingDays.toInt())
            val tomorrowStart = Calendar.getInstance()
            tomorrowStart.time = tomorrow.time
            tomorrowStart.set(Calendar.HOUR_OF_DAY, 0)
            tomorrowStart.set(Calendar.MINUTE, 0)

            Log.d("MyApp", "currentTime: $currentTime")
            Log.d("MyApp", "beginTimeDate: $beginTimeDate")
            Log.d("MyApp", "currentTime.before(beginTimeDate): ${currentTime.before(beginTimeDate)}")
            Log.d("MyApp", "currentTime.before(beginTimeDate): $selectBeginTime")

            if (isAfterNextBeginTime(selectBeginTime, tomorrowStart.time)) {
                Log.d("MyApp", "Marking photos as hidden for groupId: $groupId")
                markPhotosAsHidden(groupId)
            }


        }
        fun isAfterNextBeginTime(selectBeginTime: String, beginTimeDate: Date): Boolean {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentTime = Calendar.getInstance()

            // Crie uma cópia da data atual para manipulação
            val currentDate = Calendar.getInstance()
            currentDate.time = currentTime.time

            // Configure a hora do próximo beginTime na cópia da data atual
            val beginTimeParts = selectBeginTime.split(":")
            if (beginTimeParts.size == 2) {
                currentDate.set(Calendar.HOUR_OF_DAY, beginTimeParts[0].toInt())
                currentDate.set(Calendar.MINUTE, beginTimeParts[1].toInt())
                currentDate.set(Calendar.SECOND, 0)
            }

            // Compare as datas considerando também as horas
            return currentTime.after(currentDate.time) && currentTime.before(beginTimeDate)
        }

        fun getRemainingDaysFromSelectDays(): Long {
            // Você precisa implementar a lógica para calcular os dias restantes com base em `selectDays`
            // Isso depende de como `selectDays` é configurado, você pode mapear as opções para números de dias.
            // Vou adicionar um exemplo simples aqui.

            val sharedPreferences = requireContext().getSharedPreferences(
                "MyPreferences",
                Context.MODE_PRIVATE
            )
            val selectDaysString = sharedPreferences.getString("selectDays", "1 Days")

            return when (selectDaysString) {
                "1 Days" -> 1
                "3 Days" -> 3
                "7 Days" -> 7
                "30 Days" -> 30
                "Unlimited" -> Long.MAX_VALUE
                else -> 0
            }
        }

        private fun isCurrentTimeAfterBeginTime(beginTime: String?): Boolean {
            if (beginTime != null) {
                val currentTime = Calendar.getInstance()
                val beginTimeParts = beginTime.split(":")
                if (beginTimeParts.size == 2) {
                    val beginHour = beginTimeParts[0].toInt()
                    val beginMinute = beginTimeParts[1].toInt()

                    // Configure a hora e o minuto de beginTime
                    val beginCalendar = Calendar.getInstance()
                    beginCalendar.set(Calendar.HOUR_OF_DAY, beginHour)
                    beginCalendar.set(Calendar.MINUTE, beginMinute)
                    beginCalendar.set(Calendar.SECOND, 0)

                    return currentTime.after(beginCalendar)
                }
            }
            return false
        }


        fun mapDaysToNumber(daysString: String): Long {
            when (daysString) {
                "1 Days" -> return 1
                "3 Days" -> return 3
                "7 Days" -> return 7
                "30 Days" -> return 30
                "Unlimited" -> return Long.MAX_VALUE
                else -> return 0
            }
        }

        private fun markPhotosAsHidden(groupId: String) {
            // Acesse o Firestore e marque as fotos do grupo como ocultas
            val firestore = FirebaseFirestore.getInstance()
            val photosCollection = firestore.collection("photos")

            photosCollection
                .whereEqualTo("groupId", groupId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            // Marque cada foto como oculta no Firestore
                            val docRef = photosCollection.document(document.id)
                            docRef.update("isDeleted", true)
                                .addOnSuccessListener {
                                    // Foto marcada como oculta com sucesso
                                }
                                .addOnFailureListener { exception ->
                                    // Trate a falha ao marcar a foto como oculta
                                }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error while marking photos as hidden: ${task.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        private fun isCurrentTimeAfterEndTime(endTime: String?): Boolean {
            if (endTime != null) {
                val currentTime = Calendar.getInstance()
                val endTimeParts = endTime.split(":")
                if (endTimeParts.size == 2) {
                    val endHour = endTimeParts[0].toInt()
                    val endMinute = endTimeParts[1].toInt()

                    // Configure a hora e o minuto de endTime
                    val endCalendar = Calendar.getInstance()
                    endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
                    endCalendar.set(Calendar.MINUTE, endMinute)
                    endCalendar.set(Calendar.SECOND, 0)

                    return currentTime.after(endCalendar)
                }
            }
            return false
        }


        private fun clearPhotoUriFromSharedPreferences(groupId: String) {
            val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove(groupId)
            editor.apply()
        }
    }


}