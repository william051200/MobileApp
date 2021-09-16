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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentUserLoginBinding
import my.tarc.mobileapp.model.AppPreferences
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.User
import my.tarc.mobileapp.viewmodel.UserViewModel


class UserLoginFragment : Fragment() {
    // Binding
    private var _binding: FragmentUserLoginBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    // Firestore database
    private val db = Firebase.firestore

    // Set login user
    private val userViewModel: UserViewModel by activityViewModels()

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
        binding.userLoginTxtEmail.setText("")
        binding.userLoginTxtPassword.setText("")

        // Initialize Firebase Auth
        auth = Firebase.auth

        if (AppPreferences.isLogin) {
            // Directly login the user if previous session is present
            logIn(AppPreferences.email, AppPreferences.password)
        } else {
            // Login to user account
            binding.userLoginBtnLogin.setOnClickListener {
                if (!validation()) logIn(
                    binding.userLoginTxtEmail.text.toString(),
                    binding.userLoginTxtPassword.text.toString()
                )
            }
        }

        // Switch to admin login
        binding.userLoginBtnAdminLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userLoginFragment_to_adminLoginFragment)
        }

        // Switch to register
        binding.userLoginBtnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_userLoginFragment_to_userRegisterFragment)
        }
    }

    private fun validation(): Boolean {
        var email = binding.userLoginTxtEmail.text.toString()
        var password = binding.userLoginTxtPassword.text.toString()
        var error = false

        if (email.isEmpty()) {
            Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            error = true
        } else if (password.isEmpty()) {
            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
            error = true
        } else if (email == "admin@gmail.com") {
            Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            error = true
        }

        return error
    }

    private fun logIn(email: String, password: String) {

        // Login to Firebase auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Log.e("Firebase Auth", "Login success")
                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()

                getUserFromFirestore(email, password)

                // Store user's session when successfully logged in
                AppPreferences.isLogin = true
                AppPreferences.email = email
                AppPreferences.password = password

                findNavController().navigate(R.id.action_userLoginFragment_to_facilityCategory)
                (activity as AppCompatActivity?)!!.supportActionBar!!.show()
            } else {
                Log.e("Firebase Auth", "Login fail")
                Toast.makeText(context, "Wrong email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserFromFirestore(email: String, password: String) {
        var userFavouriteFacilityList = ArrayList<Facility>()

        // Get user
        db.collection("user").document(email).get().addOnSuccessListener {
            var fullName: String = it.get("full_name").toString()
            var facilityList: ArrayList<String> = it.get("favourite_facility") as ArrayList<String>

            // Get favourite facility
            if (facilityList.size > 0) {
                facilityList.map { eachFacility ->
                    db.collection("facility").document(eachFacility).get()
                        .addOnSuccessListener { collectedFacility ->
                            var facility = Facility(collectedFacility.id)
                            userFavouriteFacilityList.add(facility)
                        }
                }
            }

            // Set user to user view model
            var user = User(email, fullName, "user", userFavouriteFacilityList, password)
            userViewModel.setUser(user)
        }
    }
}