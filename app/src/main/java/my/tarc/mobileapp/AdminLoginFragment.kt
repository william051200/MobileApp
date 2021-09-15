package my.tarc.mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentAdminBinding


class AdminLoginFragment : Fragment() {
    // Binding
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Switch to user login
        binding.userlogin.setOnClickListener {
            findNavController().navigate(R.id.action_adminLoginFragment_to_userLoginFragment)
        }

        // Login as admin, navigate to admin category
        binding.adminhomepage.setOnClickListener {
            findNavController().navigate(R.id.action_adminLoginFragment_to_adminHomepageFragment)
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }
    }
}