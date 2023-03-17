import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.guga.supp4youapp.R
import com.guga.supp4youapp.databinding.FragmentAccessBinding

class AccessFragment : Fragment(R.layout.fragment_access) {

    private lateinit var binding: FragmentAccessBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccessBinding.bind(view)

    }
}