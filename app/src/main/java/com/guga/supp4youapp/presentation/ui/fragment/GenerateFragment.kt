package com.guga.supp4youapp.presentation.ui.fragment

import Validator.validateCode
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentGenerateBinding
import com.guga.supp4youapp.presentation.ui.camera.CameraActivity

class GenerateFragment : Fragment(R.layout.fragment_generate) {

    private var _binding: FragmentGenerateBinding? = null
    private val binding get() = _binding!!
    private var spaceId: String? = null
    private var personName: String? = null
    private var groupName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenerateBinding.inflate(inflater, container, false)
        val code = validateCode()
        binding.tvEntercode.text = code
        spaceId = arguments?.getString("spaceId")
        personName = arguments?.getString("personName")
        groupName = arguments?.getString("groupName")
        binding.tvEntercode.text = spaceId

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvContinue.setOnClickListener {
            spaceId?.let { id ->
                val intent = Intent(requireContext(), CameraActivity::class.java)
                intent.putExtra("groupId", id) // Passing ID to CameraActivity
                intent.putExtra("personName", personName) // Passing the name to CameraActivity
                intent.putExtra("groupName", groupName)
                startActivity(intent)
            }
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
