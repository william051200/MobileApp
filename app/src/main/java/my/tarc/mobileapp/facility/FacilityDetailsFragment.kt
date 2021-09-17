package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityDetailsBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel
import my.tarc.mobileapp.viewmodel.UserViewModel
import java.util.*
import kotlin.collections.ArrayList

class FacilityDetailsFragment : Fragment() {

    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    // view model
    private val facilityViewModel: FacilityViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    // Binding
    private var _binding: FragmentFacilityDetailsBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private lateinit var images: ArrayList<Drawable>

    // Current position of selected image
    private var position = 0

    private lateinit var address: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityDetailsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Facility Details"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFavourite()
        loadFacilityDetail()

        // Image switcher
        binding.facilityDetailImageSwitcher.setFactory { ImageView(activity?.applicationContext) }

        // Store image list
        images = ArrayList()

        // favourite this facility
        binding.facilityDetailBtnFavourite.setOnClickListener {
            binding.facilityDetailBtnUnfavourite.visibility = View.VISIBLE
            binding.facilityDetailBtnFavourite.visibility = View.INVISIBLE
            handleFavouriteFacility("favourite")
        }

        // unfavourite this facility
        binding.facilityDetailBtnUnfavourite.setOnClickListener {
            binding.facilityDetailBtnUnfavourite.visibility = View.INVISIBLE
            binding.facilityDetailBtnFavourite.visibility = View.VISIBLE
            handleFavouriteFacility("unfavourite")
        }

        // next image
        binding.facilityDetailBtnNextImage.setOnClickListener {
            if (position < images.size - 1) {
                position++
                binding.facilityDetailImageSwitcher.setImageDrawable(images[position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images", Toast.LENGTH_SHORT).show()
            }
        }

        // previous image
        binding.facilityDetailBtnPreviousImage.setOnClickListener {
            if (position > 0) {
                position--
                binding.facilityDetailImageSwitcher.setImageDrawable(images!![position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images", Toast.LENGTH_SHORT).show()
            }
        }

        // Submit a feedback
        binding.facilityDetailBtnSubmitfeedback.setOnClickListener {
            submitFeedback()
        }

        // Navigate to map
        binding.facilityDetailBtnNavigate.setOnClickListener {
            val uri: Uri =
                Uri.parse("https://www.google.co.in/maps/dir/Your Location/${address}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun loadFacilityDetail() {
        var id: String = facilityViewModel.selectedFacility.value!!.id
        var name: String
        var operatingHours: String
        var feature: String

        db.collection("facility").document(id).get().addOnSuccessListener {
            name = it.get("name") as String
            feature = it.get("oku_feature") as String

            // Address
            var street: String = it.get("address_street") as String
            var postcode: String = it.get("address_postcode") as String
            var city: String = it.get("address_city") as String
            var state: String = it.get("address_state") as String
            address = "$street, $postcode $city, $state"

            // Operating hours
            var startTime: String = it.get("starting_hour") as String
            var closeTime: String = it.get("closing_hour") as String
            operatingHours = "$startTime - $closeTime"

            binding.facilityDetailTxtName.text = name
            binding.facilityDetailTxtAddress.text = address
            binding.facilityDetailTxtOperationHours.text = operatingHours
            binding.facilityDetailTxtFeature.text = feature
        }

        storageRef.child(id).listAll().addOnSuccessListener {
            var size: Int = it.items.size
            for (i in 0 until size) {
                var bmp: Bitmap? = null
                val ONE_MEGABYTE: Long = 1024 * 1024
                val imageReference = storageRef.child(id).child("$i.png")

                imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val drawable: Drawable = BitmapDrawable(bmp)
                    images?.add(drawable)
                    binding.facilityDetailImageSwitcher.setImageDrawable(images[0])
                }
            }
        }
    }

    private fun loadFavourite() {
        var email: String = userViewModel.activeUser.value!!.email
        var facilityid: String = facilityViewModel.selectedFacility.value!!.id
        var favouriteList = ArrayList<String>()

        db.collection("user").document(email).get().addOnSuccessListener {
            favouriteList = it.get("favourite_facility") as ArrayList<String>
            if (favouriteList.size > 0) {
                favouriteList.map { eachFavourite ->
                    if (eachFavourite == facilityid) {
                        binding.facilityDetailBtnUnfavourite.visibility = View.VISIBLE
                        binding.facilityDetailBtnFavourite.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun handleFavouriteFacility(type: String) {
        var email: String = userViewModel.activeUser.value!!.email
        var facilityid: String = facilityViewModel.selectedFacility.value!!.id
        var favouriteList = ArrayList<String>()

        db.collection("user").document(email).get().addOnSuccessListener {
            favouriteList = it.get("favourite_facility") as ArrayList<String>

            if (type == "favourite") {
                favouriteList.add(facilityid)
                db.collection("user").document(email).update("favourite_facility", favouriteList)
                Toast.makeText(context, "Added to your favourite", Toast.LENGTH_SHORT).show()
            } else {
                favouriteList.remove(facilityid)
                db.collection("user").document(email).update("favourite_facility", favouriteList)
                Toast.makeText(context, "Removed from your favourite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Open submit feedback dialog
    private fun submitFeedback() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_submit_feedback, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val feedbackID: String = UUID.randomUUID().toString()
        val btnSubmit: Button = view.findViewById(R.id.dialogSubmitFeedback_btnSubmit)
        val btnCancel: Button = view.findViewById(R.id.dialogSubmitFeedback_btnCancel)
        var spinnerType: Spinner = view.findViewById(R.id.dialogSubmitFeedback_spinnerFeedbackType)
        var txtComment: TextView = view.findViewById(R.id.dialogSubmitFeedback_txtComment)
        var selectedType: String = spinnerType.selectedItem.toString()
        val params = btnSubmit.layoutParams as ConstraintLayout.LayoutParams

        // Incorrect name
        val lblName: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblName)
        val txtName: TextView = view.findViewById(R.id.dialogSubmitFeedback_txtName)

        // Incorrect operation hours
        val lblStartTime: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblStartTime)
        val lblCloseTime: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblCloseTime)
        val spinnerStartTime: Spinner =
            view.findViewById(R.id.dialogSubmitFeedback_spinnerStartTime)
        val spinnerCloseTime: Spinner =
            view.findViewById(R.id.dialogSubmitFeedback_spinnerCloseTime)

        // Incorrect address
        val lblStreet: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblStreet)
        val lblPostCode: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblPostcode)
        val lblCity: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblCity)
        val lblState: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblState)
        val txtStreet: TextView = view.findViewById(R.id.dialogSubmitFeedback_txtStreet)
        val txtPostCode: TextView = view.findViewById(R.id.dialogSubmitFeedback_txtPostcode)
        val spinnerCity: Spinner = view.findViewById(R.id.dialogSubmitFeedback_spinnerCity)
        val spinnerState: Spinner = view.findViewById(R.id.dialogSubmitFeedback_spinnerState)

        // Incorrect OKU Feature
        val lblOKUFeature: TextView = view.findViewById(R.id.dialogSubmitFeedback_lblOKUFeature)
        val spinnerOKUFeature: Spinner = view.findViewById(R.id.dialogSubmitFeedback_spinnerFeature)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Hide all new input fields
        fun hideAll() {
            lblName.visibility = View.INVISIBLE
            txtName.visibility = View.INVISIBLE
            lblStartTime.visibility = View.INVISIBLE
            lblCloseTime.visibility = View.INVISIBLE
            spinnerStartTime.visibility = View.INVISIBLE
            spinnerCloseTime.visibility = View.INVISIBLE
            lblStreet.visibility = View.INVISIBLE
            lblPostCode.visibility = View.INVISIBLE
            lblCity.visibility = View.INVISIBLE
            lblState.visibility = View.INVISIBLE
            txtStreet.visibility = View.INVISIBLE
            txtPostCode.visibility = View.INVISIBLE
            spinnerCity.visibility = View.INVISIBLE
            spinnerState.visibility = View.INVISIBLE
            lblOKUFeature.visibility = View.INVISIBLE
            spinnerOKUFeature.visibility = View.INVISIBLE
        }

        // Handle spinner feedback type
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = spinnerType.selectedItem.toString()
                hideAll()
                if (selectedType == "Incorrect Name") {
                    lblName.visibility = View.VISIBLE
                    txtName.visibility = View.VISIBLE
                    params.topToBottom = txtName.id
                } else if (selectedType == "Incorrect Operation Hours") {
                    lblStartTime.visibility = View.VISIBLE
                    lblCloseTime.visibility = View.VISIBLE
                    spinnerStartTime.visibility = View.VISIBLE
                    spinnerCloseTime.visibility = View.VISIBLE
                    params.topToBottom = spinnerCloseTime.id
                } else if (selectedType == "Incorrect Address") {
                    lblStreet.visibility = View.VISIBLE
                    lblPostCode.visibility = View.VISIBLE
                    lblCity.visibility = View.VISIBLE
                    lblState.visibility = View.VISIBLE
                    txtStreet.visibility = View.VISIBLE
                    txtPostCode.visibility = View.VISIBLE
                    spinnerCity.visibility = View.VISIBLE
                    spinnerState.visibility = View.VISIBLE
                    params.topToBottom = spinnerState.id
                } else if (selectedType == "Incorrect OKU Feature") {
                    lblOKUFeature.visibility = View.VISIBLE
                    spinnerOKUFeature.visibility = View.VISIBLE
                    params.topToBottom = spinnerOKUFeature.id
                }
            }
        }

        // Initialise new data input
        var newName: String = ""
        var newStartingHour: String = ""
        var newClosingHour: String = ""
        var newAddressStreet: String = ""
        var newAddressPostcode: String = ""
        var newAddressCity: String = ""
        var newAddressState: String = ""
        var newOKUFeature: String = ""

        btnSubmit.setOnClickListener {
            // Validate comment
            if (txtComment.text.isEmpty()) {
                txtComment.setError("Comment cannot be empty!")
                Toast.makeText(context, "Invalid comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedType == "Incorrect Name") {
                // Validate faciltiy name
                if (txtName.text.isEmpty()) {
                    txtComment.setError("Facility name cannot be empty!")
                    Toast.makeText(context, "Invalid name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                newName = txtName.text.toString()

            } else if (selectedType == "Incorrect Operation Hours") {
                newStartingHour = spinnerStartTime.selectedItem.toString()
                newClosingHour = spinnerCloseTime.selectedItem.toString()

            } else if (selectedType == "Incorrect Address") {
                // Validate address street
                if (txtStreet.text.isEmpty()) {
                    txtComment.setError("Address street cannot be empty!")
                    Toast.makeText(context, "Invalid street", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Validate address postcode
                if (txtPostCode.text.isEmpty()) {
                    txtComment.setError("Address postcode cannot be empty!")
                    Toast.makeText(context, "Invalid postcode", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                newAddressStreet = txtStreet.text.toString()
                newAddressPostcode = txtPostCode.text.toString()
                newAddressCity = spinnerCity.selectedItem.toString()
                newAddressState = spinnerState.selectedItem.toString()

            } else if (selectedType == "Incorrect OKU Feature") {
                newOKUFeature = spinnerOKUFeature.selectedItem.toString()
            }

            // Generate new feedback
            val feedback = hashMapOf(
                "type" to spinnerType.selectedItem.toString(),
                "comment" to txtComment.text.toString(),
                "facility" to facilityViewModel.selectedFacility.value?.id.toString(),
                "name" to newName,
                "starting_hour" to newStartingHour,
                "closing_hour" to newClosingHour,
                "address_street" to newAddressStreet,
                "address_postcode" to newAddressPostcode,
                "address_city" to newAddressCity,
                "address_state" to newAddressState,
                "oku_feature" to newOKUFeature,
            )
            db.collection("feedback").document(feedbackID).set(feedback).addOnSuccessListener {
                Toast.makeText(context, "Feedback submitted", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
    }
}