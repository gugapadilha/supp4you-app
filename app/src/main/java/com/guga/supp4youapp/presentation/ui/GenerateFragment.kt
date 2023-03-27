import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentAccessBinding
import com.guga.supp4youapp.databinding.FragmentGenerateBinding

class GenerateFragment : Fragment(R.layout.fragment_generate) {

    private lateinit var binding: FragmentGenerateBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentGenerateBinding.bind(view)

    }
}