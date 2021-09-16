package my.tarc.mobileapp.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentAdminLoginBinding
import my.tarc.mobileapp.model.AppPreferences
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.User
import my.tarc.mobileapp.viewmodel.UserViewModel

class AdminLoginFragment : Fragment() {
    // Binding
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    // Set login admin
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.adminLoginTxtEmail.setText("")
        binding.adminLoginTxtPassword.setText("")


        // Initialize Firebase Auth
        auth = Firebase.auth

        if (AppPreferences.isLogin) {
            // Directly login the user if previous session is present
            logIn(AppPreferences.email, AppPreferences.password)
            binding.adminuser.visibility = View.INVISIBLE
            binding.adminpass.visibility = View.INVISIBLE
            binding.adminLoginBtnLogIn.visibility = View.INVISIBLE
            binding.textViewNotAdmin.visibility = View.INVISIBLE
            binding.adminLoginBtnUserLogin.visibility = View.INVISIBLE
            binding.textView3.text = "Logging in, please wait for a moment"
        } else {
            // Login to user account
            binding.adminLoginBtnLogIn.setOnClickListener {
                if (!validation()) logIn(
                    binding.adminLoginTxtEmail.text.toString(),
                    binding.adminLoginTxtPassword.text.toString()
                )
            }
        }
        // Switch to user login
        binding.adminLoginBtnUserLogin.setOnClickListener {
            findNavController().navigate(R.id.action_adminLoginFragment_to_userLoginFragment)
        }
    }

    private fun validation(): Boolean {
        var email = binding.adminLoginTxtEmail.text.toString()
        var password = binding.adminLoginTxtPassword.text.toString()
        var error = false

        if (email.isEmpty()) {
            Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            error = true
        } else if (password.isEmpty()) {
            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
            error = true
        } else if (email != "admin@gmail.com") {
            Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            error = true
        }

        return error
    }

    private fun logIn(email: String, password: String) {

        var emptyList = ArrayList<Facility>()

        // Login to Firebase auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Log.e("Firebase Auth", "Login success")
                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()

                // Set admin to user view model
                var user = User("", "", "admin", emptyList, "")
                userViewModel.setUser(user)

                findNavController().navigate(R.id.action_adminLoginFragment_to_adminHomepageFragment)
                (activity as AppCompatActivity?)!!.supportActionBar!!.show()
            } else {
                Log.e("Firebase Auth", "Login fail")
                Toast.makeText(context, "Wrong email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}