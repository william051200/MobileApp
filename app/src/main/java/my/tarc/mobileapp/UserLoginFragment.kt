package my.tarc.mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentLoginPageBinding

class UserLoginFragment : Fragment() {
    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Navigate to user profile page
        binding.loginUserBtnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userLoginFragment_to_facilityCategory)
        }
    }

}