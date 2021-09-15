package my.tarc.mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.databinding.FragmentUserLoginBinding


class UserLoginFragment : Fragment() {
    // Binding
    private var _binding: FragmentUserLoginBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserLoginBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        // Login to user account
        binding.loginUserBtnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userLoginFragment_to_facilityCategory)
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }

        // Switch to admin login
        binding.adminloginnav.setOnClickListener {
            findNavController().navigate(R.id.action_userLoginFragment_to_adminLoginFragment)
        }

        // Switch to register
        binding.signupnav.setOnClickListener {
            findNavController().navigate(R.id.action_userLoginFragment_to_userRegisterFragment)
        }
    }

}