import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentAccessBinding

class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)

        binding.tvEnterspace.setOnClickListener {
            //navigation to next screen here
        }

        fun validateCode(){
            if(binding.tvEntercode.text.toString().isBlank() || binding.tvEntercode.text?.length != 6){
                Toast.makeText(requireContext(), "Invalid Credencials", Toast.LENGTH_SHORT).show()
            }
            else{
                //navigation here
            }
        }
    }
}