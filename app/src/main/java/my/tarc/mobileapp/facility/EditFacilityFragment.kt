package my.tarc.mobileapp.facility

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentEditFacilityBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel
import my.tarc.mobileapp.viewmodel.UserViewModel
import android.widget.ArrayAdapter




class EditFacilityFragment : Fragment() {
    // Connect to Facility viewModel
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    //Binding fragment
    private var _binding: FragmentEditFacilityBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private lateinit var images: ArrayList<Drawable>

    // Current position of selected image
    private var position = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditFacilityBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Edit Facility"
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data from firebase
        getFacilityDetailsFromFirebase()

        // Store image list
        images = ArrayList()

        //Setup Image Switcher
        binding.imageViewEditFacilityImage.setFactory { ImageView(activity?.applicationContext) }

        //Next Image
        binding.btnNextImg.setOnClickListener {
            if (position < images.size - 1) {
                position++
                binding.imageViewEditFacilityImage.setImageDrawable(images[position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        //Previous Image
        binding.btnPreviousImg.setOnClickListener {
            if (position > 0) {
                position--
                binding.imageViewEditFacilityImage.setImageDrawable(images[position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to facility details
        binding.btnEditFacilityCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // Navigate to Admin Facility List
        binding.btnEditFacilitySave.setOnClickListener {
            if(!facilityValidation()) updateFacilityDetails()
        }

    }

    private fun getFacilityDetailsFromFirebase(){
        // Get the facility id selected from the facility list
        var facId: String = facilityViewModel.selectedFacility.value!!.id

        db.collection("facility").document(facId).get()
            .addOnSuccessListener {
                var facName: String = it.get("name") as String
                var address: String = it.get("address_street") as String
                var postCode: String = it.get("address_postcode") as String
                var closinghrs: String = it.get("closing_hour") as String

                var adapter = binding.spinnerEditCT.adapter as ArrayAdapter<String>
                var position = adapter.getPosition(closinghrs)
                binding.spinnerEditCT.setSelection(position)

//                val spinnerCT: Adapter = binding.spinnerEditCT.adapter
//                spinnerCT.setSelection(((ArrayAdapter)spinnerCT.getAdapter().getPosition(closinghrs)))


                binding.txtEditFacilityName.setText(facName)
                binding.txtEditFacilityAddress.setText(address)
                binding.txtEditFacilityZipCode.setText(postCode)


            }.addOnFailureListener{
                Toast.makeText(this.context, "Failed to retrieve the facility data", Toast.LENGTH_SHORT).show()
            }

        storageRef.child(facId).listAll().addOnSuccessListener {
            var size: Int = it.items.size
            for (i in 0 until size) {
                var bmp: Bitmap? = null
                val ONE_MEGABYTE: Long = 1024 * 1024
                val imageReference = storageRef.child(facId).child("$i.png")

                imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val drawable: Drawable = BitmapDrawable(bmp)
                    images?.add(drawable)
                    binding.imageViewEditFacilityImage.setImageDrawable(images[0])
                }
            }
        }
    }

    private fun updateFacilityDetails(){
        // Get the facility id selected from the facility list
        var facId: String = facilityViewModel.selectedFacility.value!!.id

        var facName: String = binding.txtEditFacilityName.text.toString()
        var startTime: String = binding.spinnerEditST.selectedItem.toString()
        var closeTime: String = binding.spinnerEditCT.selectedItem.toString()
        var okuFeature: String = binding.spinnerEditOKUFeature.selectedItem.toString()
        var facAddress: String = binding.txtEditFacilityAddress.text.toString()
        var facCity: String = binding.EditFacilitySpinnerCity.selectedItem.toString()
        var facState: String = binding.EditFacilitySpinnerState.selectedItem.toString()
        var facPostCode: String = binding.txtEditFacilityZipCode.text.toString()
        var facCategory: String = binding.EditFacilitySpinnerCategory.selectedItem.toString()

        db.collection("facility").document(facId).update("name", facName,
            "starting_hour", startTime,
            "address_street", facAddress,
            "closing_hour", closeTime,
            "oku_feature", okuFeature,
            "address_city", facCity,
            "address_state", facState,
            "address_postcode", facPostCode,
            "category", facCategory)
            .addOnSuccessListener {
            Toast.makeText(this.context, "Facility Updated ", Toast.LENGTH_SHORT).show()
                // Navigate back to facility details
                findNavController().popBackStack()
        }
    }

    // Facility Details Validation
    private fun facilityValidation(): Boolean {
        val textFacilityName: TextView = binding.txtEditFacilityName
        val textStreetAddress: TextView = binding.txtEditFacilityAddress
        val textZipCode: TextView = binding.txtEditFacilityZipCode
        var error: Boolean = false

        if (textFacilityName.text.isEmpty()) {
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
        }
        return error
    }
}