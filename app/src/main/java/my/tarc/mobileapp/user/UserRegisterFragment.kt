package my.tarc.mobileapp.user

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentRegisterBinding
import java.util.regex.Pattern


class UserRegisterFragment : Fragment() {
    // Binding
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    // Firestore database
    private val db = Firebase.firestore
    private val userRef = db.collection("user")

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
        binding.registerTxtFullName.setText("")
        binding.registerTxtEmail.setText("")
        binding.registerTxtPassword.setText("")


        // Initialize Firebase Auth
        auth = Firebase.auth

        // Register a new account
        binding.registerBtnSignUp.setOnClickListener {
            if (!validation()) createNewAccount()
        }

        // Switch to admin login
        binding.registerBtnAdminLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userRegisterFragment_to_adminLoginFragment)
        }

        // Switch to user login
        binding.registerBtnUserLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userRegisterFragment_to_userLoginFragment)
        }
    }

    private fun validation(): Boolean {
        var fullName = binding.registerTxtFullName.text.toString()
        var email = binding.registerTxtEmail.text.toString()
        var password = binding.registerTxtPassword.text.toString()
        var error = false

        // Email pattern
        val pattern: Pattern = Patterns.EMAIL_ADDRESS

        if (fullName.isEmpty()) {
            Toast.makeText(context, "Invalid full name", Toast.LENGTH_SHORT).show()
            binding.registerTxtFullName.setError("Full name cannot be empty!")
            error = true
        } else if (email.isEmpty()) {
            Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            binding.registerTxtEmail.setError("Email cannot be empty!")
            error = true
        } else if (!pattern.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            binding.registerTxtEmail.setError("Email must be in correct format!")
            error = true
        } else if (password.isEmpty()) {
            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
            binding.registerTxtPassword.setError("Password cannot be empty!")
            error = true
        } else if (password.length < 8) {
            Toast.makeText(context, "Password is too weak", Toast.LENGTH_SHORT).show()
            binding.registerTxtPassword.setError("Password must be more than 8 characters!")
            error = true
        }

        return error
    }

    private fun createNewAccount() {
        var fullName = binding.registerTxtFullName.text.toString()
        var email = binding.registerTxtEmail.text.toString()
        var password = binding.registerTxtPassword.text.toString()
        var emptyArray = ArrayList<String>()

        // Create new user in Firebase auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Log.e("Firebase Auth", "Success creating user in auth")

                // Generate new user
                val user = hashMapOf(
                    "user_type" to "user",
                    "full_name" to fullName,
                    "favourite_facility" to emptyArray,
                )

                // Create new user document in Firestore
                userRef.document(email).set(user).addOnSuccessListener {
                    Log.e("Firebase Auth", "Success creating user in Firestore")
                    Toast.makeText(this.context, "Sign up success!", Toast.LENGTH_SHORT).show()
                    // Switch to user login once success register
                    findNavController().navigate(R.id.action_userRegisterFragment_to_userLoginFragment)
                }.addOnFailureListener {
                    Log.e("Firestore", "Failed to create user in Firestore!")
                }
            } else {
                Toast.makeText(context, "Email is already exist", Toast.LENGTH_SHORT).show()
                Log.e("Firebase Auth", "Failed to create user in firebase Auth!")
            }
        }
    }
}