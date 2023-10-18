package com.guga.supp4youapp.presentation.ui.fragment

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.data.remote.database.Space
import com.guga.supp4youapp.databinding.FragmentAccessBinding
import com.guga.supp4youapp.presentation.ui.adapter.CustomSpinnerAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding
    private val createSpace = Firebase.firestore.collection("create")
    private val selectedTime: Calendar = Calendar.getInstance()
    private var beginTime: String? = null
    private var endTime: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)
        val personName = arguments?.getString("personName")

        binding.tvCreateSpace.setOnClickListener {
            val groupName = binding.tvGroupName.text.toString()
            val selectedDays = binding.spDays.selectedItem.toString()
            val selectBeginTime = beginTime
            val selectEndTime = endTime
            val currentTimestamp = System.currentTimeMillis() // Obter o timestamp atual

            if (groupName.isNotEmpty() && selectBeginTime != null && selectEndTime != null) {
                // Verifique se o nome do grupo não está vazio e ambos os tempos foram selecionados

                val space = Space(groupName, selectedDays, selectBeginTime, selectEndTime, currentTimestamp)

                binding.progressBar.visibility = View.VISIBLE

                // Usando um CoroutineScope para criar o espaço e obter o ID gerado
                CoroutineScope(Dispatchers.Main).launch {
                    val spaceId = createSpace(space)

                    delay(1000)
                    binding.progressBar.visibility = View.GONE

                    // Criar um Bundle para passar o nome para a GenerateFragment
                    val bundle = Bundle()
                    bundle.putString("spaceId", spaceId)
                    bundle.putString("personName", personName)
                    bundle.putString("groupName", groupName)
                    bundle.putString("selectBeginTime", selectBeginTime) // Adicione o horário de início
                    bundle.putString("selectDays", selectedDays) // Adicione os dias selecionados
                    bundle.putString("selectEndTime", selectEndTime) // Adicione o horário de término
                    bundle.putLong("timestamp", currentTimestamp) // Adicione o timestamp

                    findNavController().navigate(R.id.action_accessFragment_to_generateFragment, bundle)
                }
            } else {
                // Exiba uma mensagem de erro se algum dos campos estiver faltando
                if (groupName.isEmpty()) {
                    Toast.makeText(requireContext(), "You need to enter a group name", Toast.LENGTH_SHORT).show()
                } else if (selectBeginTime == null) {
                    Toast.makeText(requireContext(), "You need to set up the begin time", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "You need to set up the end time", Toast.LENGTH_SHORT).show()
                }
            }
        }



        val daysArray = resources.getStringArray(R.array.days).toList()
        val customAdapter = CustomSpinnerAdapter(requireContext(), daysArray)
        binding.spDays.adapter = customAdapter

        binding.spDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (adapterView != null) {
                    val selectedItem = adapterView.getItemAtPosition(position)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spStartTime.setOnClickListener {
            showTimePickerDialog { time ->
                beginTime = time
                binding.spStartTime.text = time
            }
        }

        binding.spEndTime.setOnClickListener {
            showTimePickerDialog { time ->
                endTime = time
                binding.spEndTime.text = time
            }
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (beginTime != null) {
            binding.spStartTime.text = beginTime
        }

        if (endTime != null) {
            binding.spEndTime.text = endTime
        }
    }

    private fun showTimePickerDialog(callback: (String) -> Unit) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val horaFormatada = String.format("%02d:%02d", hourOfDay, minute)
                callback(horaFormatada)
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }


    suspend fun createSpace(space: Space): String {
        val result = createSpace.add(space).await()
        createSpace.document(result.id).update("id", result.id).await()
        return result.id
    }
}
