package com.guga.supp4youapp.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

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

        binding.tvLoginspace.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetDialogFragment()
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        binding.tvCreatespace.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_accessFragment)
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed() // Volta para a tela anterior
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed() // Volta para a tela anterior
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
        }
    }
}