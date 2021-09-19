package my.tarc.mobileapp.facility

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentUploadFacilityBinding
import my.tarc.mobileapp.viewmodel.UserViewModel
import java.util.*
import kotlin.collections.ArrayList


class UploadFacilityFragment : Fragment() {

    // Firestore database
    private val db = Firebase.firestore
    private val facilityRef = db.collection("facility")

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    //Binding fragment
    private var _binding: FragmentUploadFacilityBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private var images: ArrayList<Uri?>? = null

    // Current position of selected image
    private var position = 0

    //request code to pick image
    private val PICK_IMAGES_CODE = 0

    // view model
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUploadFacilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Upload Facility"
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_uploadFacility).isVisible = false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Store image list
        images = ArrayList()

        //Setup Image Switcher
        binding.imgSwitcherFacility.setFactory { ImageView(activity?.applicationContext) }

        //Pick Image
        binding.btnPickFacilityImage.setOnClickListener {
            pickImagesIntent()
        }

        //Next Image >
        binding.btnNextImg.setOnClickListener {
            if (position < images!!.size - 1) {
                position++
                binding.imgSwitcherFacility.setImageURI(images!![position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        //Previous Image <
        binding.btnPreviousImg.setOnClickListener {
            if (position > 0) {
                position--
                binding.imgSwitcherFacility.setImageURI(images!![position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        // Validation and Upload facility data to firebase
        binding.btnUploadFacilityUpload.setOnClickListener {
            if (!facilityValidation()) newFacility()
        }

        // Navigate back to Facility Category
        binding.btnUploadFacilityCancel.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    // Pick Image
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun pickImagesIntent() {
        val intent = Intent()
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE)
    }

    // Display Image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_CODE && resultCode == RESULT_OK) {

            if (data!!.clipData != null) {
                //picked multiple images
                //get number of picked images
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    //add image to list
                    images!!.add(imageUri)
                }
                // set first image from array list to image switcher
                binding.imgSwitcherFacility.setImageURI(images!![0])
                position = 0
            } else {
                //picked single image
                val imageUri = data.data
                //set image to image switcher
                binding.imgSwitcherFacility.setImageURI(imageUri)
                position = 0
            }
        }
    }

    // Facility Details Validation
    private fun facilityValidation(): Boolean {
        val textFacilityName: TextView = binding.txtFacilityName
        val textStreetAddress: TextView = binding.txtFacilityAddress
        val textZipCode: TextView = binding.txtFacilityZipCode
        val facImg: String = images?.size.toString()
        var error: Boolean = false

        if (facImg == "0") {
            Toast.makeText(activity, "Please upload image(s)", Toast.LENGTH_SHORT).show()
            error = true
        }
        else if (textFacilityName.text.isEmpty()) {
            Toast.makeText(activity, "Invalid Facility Name", Toast.LENGTH_SHORT).show()
            textFacilityName.requestFocus()
            error = true
        } else if (textStreetAddress.text.isEmpty()) {
            Toast.makeText(activity, "Invalid Street Address", Toast.LENGTH_SHORT).show()
            textStreetAddress.requestFocus()
            error = true
        } else if (textZipCode.text.isEmpty() || textZipCode.text.length < 5) {
            Toast.makeText(activity, "Invalid Zip Code", Toast.LENGTH_SHORT).show()
            textZipCode.requestFocus()
            error = true
        } else if (position < images!!.size - 1){
            Toast.makeText(activity, "Please upload image(s)", Toast.LENGTH_SHORT).show()
            error = true
        }
        return error
    }

    private fun newFacility() {
//        Log.e("pick image", images?.size.toString())

        val facilityName = binding.txtFacilityName.text.toString()
        val startTime = binding.spinnerST.selectedItem.toString()
        val closeTime = binding.spinnerCT.selectedItem.toString()
        val okuFeature = binding.txtOKUFeature.selectedItem.toString()
        val streetAddress = binding.txtFacilityAddress.text.toString()
        val city = binding.spinnerCity.selectedItem.toString()
        val state = binding.spinnerState.selectedItem.toString()
        val zipCode = binding.txtFacilityZipCode.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val facilityID = UUID.randomUUID()

        var status = "Pending"
        if (userViewModel.activeUser.value!!.userType == "admin") {
            status = "Approved"
        }

        // Create New Facility
        val facility = hashMapOf(
            "name" to facilityName,
            "starting_hour" to startTime,
            "closing_hour" to closeTime,
            "oku_feature" to okuFeature,
            "address_street" to streetAddress,
            "address_city" to city,
            "address_state" to state,
            "address_postcode" to zipCode,
            "status" to status,
            "feedbacks" to ArrayList<String>(),
            "category" to category
        )

        images?.forEachIndexed { index, image ->
            storageRef.child(facilityID.toString()).child("$index.png").putFile(image!!)
        }

        facilityRef.document(facilityID.toString()).set(facility).addOnSuccessListener {
            Toast.makeText(this.context, "Upload successful!", Toast.LENGTH_SHORT).show()

            // Navigate back to facility category once uploaded
            findNavController().popBackStack()
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to create facility in Firestore!")
        }
    }
}

