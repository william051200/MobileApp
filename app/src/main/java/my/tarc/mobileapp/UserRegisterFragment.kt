package my.tarc.mobileapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentRegisterBinding
import my.tarc.mobileapp.databinding.FragmentUserLoginBinding


class UserRegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to facility category page
        binding.adminlogin.setOnClickListener {
            findNavController().navigate(R.id.action_userRegisterFragment_to_adminLoginFragment)
        }

        binding.userlogin.setOnClickListener{
            findNavController().navigate(R.id.action_userRegisterFragment_to_userLoginFragment)
        }


    }


}