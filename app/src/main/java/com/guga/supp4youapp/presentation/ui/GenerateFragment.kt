import Validator.validateCode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentGenerateBinding

class GenerateFragment : Fragment(R.layout.fragment_generate) {

    private var _binding: FragmentGenerateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenerateBinding.inflate(inflater, container, false)
        val code = validateCode()
        binding.tvEntercode.text = code
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
