package com.guga.supp4youapp.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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


class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding
    private val createSpace = Firebase.firestore.collection("create")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)
        val personName = arguments?.getString("personName")

        binding.tvCreateSpace.setOnClickListener {
            val groupName = binding.tvGroupName.text.toString()
            val selectedDays = binding.spDays.selectedItem.toString()
            val selectBeginTime = binding.spStartTime.selectedItem.toString()
            val selectEndTime = binding.spEndTime.selectedItem.toString()
            val space = Space(groupName, selectedDays, selectBeginTime, selectEndTime)

            // Usando um CoroutineScope para criar o espaço e obter o ID gerado
            CoroutineScope(Dispatchers.Main).launch {
                val spaceId = createSpace(space)

                // Criar um Bundle para passar o nome para a GenerateFragment
                val bundle = Bundle()
                bundle.putString("spaceId", spaceId)
                bundle.putString("personName", personName) // Adicionar o nome aqui

                findNavController().navigate(R.id.action_accessFragment_to_generateFragment, bundle)
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

        val timesArray = resources.getStringArray(R.array.times).toList()
        val timeAdapter = CustomSpinnerAdapter(requireContext(), timesArray)
        binding.spStartTime.adapter = timeAdapter
        binding.spEndTime.adapter = timeAdapter

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }
    private suspend fun createSpace(space: Space): String {
        val result = createSpace.add(space).await()
        createSpace.document(result.id).update("id", result.id).await()
        return result.id
    }

}