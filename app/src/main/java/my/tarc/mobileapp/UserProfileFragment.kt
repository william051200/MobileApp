package my.tarc.mobileapp


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentUserProfileBinding
import java.util.jar.Manifest


class UserProfileFragment : Fragment() {
    //Binding fragment
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    //request code to pick image
    private val IMAGE_PICK_CODE = 0

    //Store Image Uri
    private var imageUri: Uri? = null

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

        //Navigate to Facility Category
        binding.btnProfileSave.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

        // Pick image
        binding.btnUserProfileEdit.setOnClickListener {
            pickProfileImage()
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
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageUri = data?.data
            binding.imageViewProfile.setImageURI(imageUri)
        }
    }
}