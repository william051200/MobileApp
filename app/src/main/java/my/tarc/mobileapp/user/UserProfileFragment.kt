package my.tarc.mobileapp.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentUserProfileBinding
import my.tarc.mobileapp.viewmodel.UserViewModel

class UserProfileFragment : Fragment() {
    // Connect To User viewModel
    private val userViewModel: UserViewModel by activityViewModels()

    //Binding fragment
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    // Firestore database
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "User Profile"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName: String = userViewModel.activeUser.value!!.name
        val email: String = userViewModel.activeUser.value!!.email
        val pwd: String = userViewModel.activeUser.value!!.password

        binding.txtUserName.setText(userName)
        binding.txtUserEmail.text = email
        binding.txtUserPwd.text = pwd

        // Navigate to Facility Category
        binding.userProfileBtnFacilityCategory.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

        // Reset Profile
        binding.btnProfileCancel.setOnClickListener {
            if (!validation()) resetUserProfile()

        }

        // Navigate to Facility Category
        binding.btnProfileSave.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        var userName: String = binding.txtUserName.text.toString()
        var email: String = userViewModel.activeUser.value!!.email

        db.collection("user").document(email).update("full_name", userName).addOnSuccessListener {
            Toast.makeText(context, "Successfully updated profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetUserProfile() {
        val userName: String = userViewModel.activeUser.value!!.name
        binding.txtUserName.setText(userName)
        Toast.makeText(context, "Successfully reset profile", Toast.LENGTH_SHORT).show()
    }

    private fun validation(): Boolean {
        var fullName = binding.txtUserName.text.toString()
        var error = false

        if (fullName.isEmpty()) {
            Toast.makeText(context, "Invalid full name", Toast.LENGTH_SHORT).show()
            error = true
            binding.txtUserEmail.setError("Full name cannot be empty!")
        }

        return error
    }
}