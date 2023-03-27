import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val spinnerItems = listOf("ULBRA", "UNESC", "UNIASSELVI", "UCS", "UNINASSAU")
    private val spinnerAdapter by lazy {
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
    private val textColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.white)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLoginspace.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_accessFragment)
        }

        binding.tvCreatespace.setOnClickListener{
            findNavController().navigate(R.id.action_detailsFragment_to_generateFragment)
        }

        binding.spinner.adapter = spinnerAdapter

        binding.spinner.post {
            (binding.spinner.selectedView as TextView).setTextColor(textColor)
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // change the color from selected text
                (parent?.getChildAt(position) as? TextView)?.setTextColor(textColor)

                // change the color from every item thats not selected as well
                for (i in 0 until parent?.childCount!!) {
                    (parent.getChildAt(i) as? TextView)?.setTextColor(textColor)
                }

                // do something with selected value
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // impl if needed
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}