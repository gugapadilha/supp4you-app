package com.guga.supp4youapp.presentation.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.guga.supp4youapp.R
import com.guga.supp4youapp.data.remote.database.Space
import com.guga.supp4youapp.databinding.FragmentAccessBinding
import com.guga.supp4youapp.presentation.ui.adapter.CustomSpinnerAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding
    private val createSpace = Firebase.firestore.collection("create")
    private val selectedTime: Calendar = Calendar.getInstance()
    private var beginTime: String? = null
    private var endTime: String? = null
    private var newGroupId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)
        val personName = arguments?.getString("personName")
        binding.tvCreateSpace.setOnClickListener {
            val groupName = binding.tvGroupName.text.toString()
            val selectedDays = binding.spDays.selectedItem.toString()
            val selectBeginTime = beginTime
            val selectEndTime = endTime
            val currentTimestamp = System.currentTimeMillis()

            if (groupName.isNotEmpty() && selectBeginTime != null && selectEndTime != null) {
                binding.progressBar.visibility = View.VISIBLE

                // Gere o novo grupo ID
                CoroutineScope(Dispatchers.Main).launch {
                    newGroupId = generateUniqueGroupID()

                    // Crie o espaço com o novo código numérico, usando o newGroupId
                    val space = Space(
                        id = newGroupId!!.toLong(),
                        groupName = groupName,
                        selectDays = selectedDays,
                        selectBeginTime = selectBeginTime,
                        selectEndTime = selectEndTime,
                        timesTamp = currentTimestamp
                    )

                    // Adicione o espaço ao Firestore
                    val spaceId = createSpace(space)

                    delay(1000)
                    binding.progressBar.visibility = View.GONE

                    val bundle = Bundle()
                    bundle.putString("spaceId", newGroupId) // Use o newGroupId como spaceId
                    bundle.putString("personName", personName)
                    bundle.putString("groupName", groupName)
                    bundle.putString("selectBeginTime", selectBeginTime)
                    bundle.putString("selectDays", selectedDays)
                    bundle.putString("selectEndTime", selectEndTime)
                    bundle.putLong("timestamp", currentTimestamp)

                    findNavController().navigate(R.id.action_accessFragment_to_generateFragment, bundle)
                }
            } else {
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

    suspend fun createSpace(space: Space): Long {
        newGroupId = generateUniqueGroupID()
        // Defina o valor do campo "id" no documento do Firestore
        space.id = newGroupId!!.toLong()
        val result = createSpace.document(newGroupId!!).set(space).await()
        return newGroupId!!.toLong()
    }


    private suspend fun generateUniqueGroupID(): String {
        val random = Random()
        while (true) {
            val numericValue = random.nextInt(10000)
            val groupId = String.format("%04d", numericValue)
            // Verifique se o grupo já existe no Firestore
            val groupDoc = createSpace.document(groupId).get().await()
            if (!groupDoc.exists()) {
                return groupId
            }
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

}
