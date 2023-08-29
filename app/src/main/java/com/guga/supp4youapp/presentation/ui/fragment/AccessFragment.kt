package com.guga.supp4youapp.presentation.ui.fragment

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding
    private val createSpace = Firebase.firestore.collection("create")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)

        binding.tvCreateSpace.setOnClickListener {
            val groupName = binding.tvGroupName.text.toString()
            val selectedDays = binding.spDays.selectedItem.toString()
            val selectBeginTime = binding.spStartTime.selectedItem.toString()
            val selectEndTime = binding.spEndTime.selectedItem.toString()
            val space = Space(groupName, selectedDays, selectBeginTime, selectEndTime)
            createSpace(space)
            findNavController().navigate(R.id.action_accessFragment_to_generateFragment)
        }

        val daysArray = resources.getStringArray(R.array.days).toList()
        val customAdapter = CustomSpinnerAdapter(requireContext(), daysArray)
        binding.spDays.adapter = customAdapter

        binding.spDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (adapterView != null) {
                    val selectedItem = adapterView.getItemAtPosition(position)
//                    val selectedText = "You selected $selectedItem"
//                    val context = requireActivity()
//                    Toast.makeText(context, selectedText, Toast.LENGTH_LONG).show()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Lógica para quando nada é selecionado
            }
        }

        val timesArray = resources.getStringArray(R.array.times).toList()
        val timeAdapter = CustomSpinnerAdapter(requireContext(), timesArray)
        binding.spStartTime.adapter = timeAdapter
        binding.spEndTime.adapter = timeAdapter

        binding.back.setOnClickListener {
            requireActivity().onBackPressed() // Volta para a tela anterior
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed() // Volta para a tela anterior
        }

    }

    private fun createSpace(space: Space) = CoroutineScope(Dispatchers.IO).launch {
        try {
            createSpace.add(space).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Successfully create space", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}