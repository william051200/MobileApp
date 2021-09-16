package my.tarc.mobileapp.user

import android.app.Activity
import android.app.Instrumentation
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ComposePathEffect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentUserProfileBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserProfileFragment : Fragment() {
    //Binding fragment
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    // Firestore database
    private val db = Firebase.firestore
    private val userRef = db.collection("user")

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("User Images")

    //request code to pick image
    private val IMAGE_PICK_CODE = 100

    //Store Image Uri
    lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Facility Category
        binding.userProfileBtnFacilityCategory.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

        // Navigate to Facility Category
        binding.btnProfileCancel.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

        // Pick image
        binding.btnUserProfileEdit.setOnClickListener {
            pickProfileImage()
        }


        // Upload to firebase
        binding.btnProfileSave.setOnClickListener{
            uploadUserProfile()

            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

    }

    private fun pickProfileImage() {
        //Intent to pick image
        val intent = Intent()
        intent.type = "image/png"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data?.data!!
            binding.imageViewProfile.setImageURI(imageUri)
        }
    }

    // Upload user profile to storage
    private fun uploadUserProfile() {
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Uploading Profile...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageRef = FirebaseStorage.getInstance().getReference("User Images/$fileName")

        storageRef.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(this.context, "Profile Updated", Toast.LENGTH_SHORT).show()
            if(progressDialog.isShowing)progressDialog.dismiss()
        }.addOnFailureListener{
            Toast.makeText(this.context, "Profile Update Failed", Toast.LENGTH_SHORT).show()
            if(progressDialog.isShowing)progressDialog.dismiss()
        }
    }
}