package my.tarc.mobileapp.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentRegisterBinding


class UserRegisterFragment : Fragment() {
    // Binding
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Switch to admin login
        binding.adminlogin.setOnClickListener {
            findNavController().navigate(R.id.action_userRegisterFragment_to_adminLoginFragment)
        }

        // Switch to user login
        binding.userlogin.setOnClickListener {
            findNavController().navigate(R.id.action_userRegisterFragment_to_userLoginFragment)
        }
    }

    fun register() {}
}