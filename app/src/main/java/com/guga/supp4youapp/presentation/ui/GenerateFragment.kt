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

    private fun validateCode(): String {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeSize = 6
        val random = java.util.Random()
        val code = CharArray(codeSize)

        for (i in 0 until codeSize) {
            code[i] = letters[random.nextInt(letters.length)]
        }

        return String(code)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
