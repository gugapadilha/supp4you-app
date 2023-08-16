import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentAccessBinding
import com.guga.supp4youapp.presentation.ui.adapter.CustomSpinnerAdapter

class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)

        binding.tvCreateSpace.setOnClickListener {
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


    }
}