package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityDetailsBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel
import my.tarc.mobileapp.viewmodel.UserViewModel
import org.w3c.dom.Text

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.facilityDetailBtnUnfavourite.visibility = View.INVISIBLE
        binding.facilityDetailTxtRatingCount.text = "Rate this facility"

        loadFacilityDetail()

        // Image switcher
        binding.facilityDetailImageSwitcher.setFactory { ImageView(activity?.applicationContext) }

        // Store image list
        images = ArrayList()

        // Rating bar
//        binding.facilityDetailRatingBar.setOnTouchListener(View.OnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                Log.e("test", v.toString())
//            }
//            return@OnTouchListener true
//        })

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
        var email: String = userViewModel.activeUser.value!!.email
        var id: String = facilityViewModel.selectedFacility.value!!.id
        var name: String
        var rating: String
        var operatingHours: String
        var feature: String

//        db.collection("user").document(email).get().addOnSuccessListener {
//            collectedFacilityId = it.get("favourite_facility") as ArrayList<String>
//        }

        db.collection("facility").document(id).get().addOnSuccessListener {
            name = it.get("name") as String
            rating = it.get("rating") as String
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
            binding.facilityDetailRatingBar.rating = rating.toFloat()
            binding.facilityDetailTxtRatingCount.text = rating
            binding.facilityDetailTxtAddress.text = address
            binding.facilityDetailTxtOperationHours.text = operatingHours
            binding.facilityDetailTxtFeature.text = feature
        }

        storageRef.child(id).listAll().addOnSuccessListener {
            var size: Int = it.items.size
            for (i in 0..size) {
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

    private fun handleFavouriteFacility(type: String) {
        var email: String = userViewModel.activeUser.value!!.email
        var collectedFacilityId = ArrayList<String>()

        db.collection("user").document(email).get().addOnSuccessListener {
            collectedFacilityId = it.get("favourite_facility") as ArrayList<String>
        }

        if (type == "favourite") {
            collectedFacilityId.add(facilityViewModel.selectedFacility.value!!.id)
            Toast.makeText(context, "Added to your favourite", Toast.LENGTH_SHORT).show()
        } else {
            collectedFacilityId.remove(facilityViewModel.selectedFacility.value!!.id)
            Toast.makeText(context, "Removed from your favourite", Toast.LENGTH_SHORT).show()
        }

        db.collection("user").document(email).update("favourite_facility", collectedFacilityId)
    }

    // Open submit feedback dialog
    private fun submitFeedback() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_submit_feedback, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val btnSubmit: Button = view.findViewById(R.id.dialogSubmitFeedback_btnSubmit)
        val btnCancel: Button = view.findViewById(R.id.dialogSubmitFeedback_btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
           var txtComment: TextView = view.findViewById(R.id.dialogSubmitFeedback_btnSubmit)
           var txtSuggestion: TextView = view.findViewById(R.id.dialogSubmitFeedback_txtSuggestion)
           var type: Spinner = view.findViewById(R.id.filterDialog_spinnerFeedbackType)

            if(txtComment.text.isEmpty()) txtComment.setError("Comment cannot be empty!")
            if(txtSuggestion.text.isEmpty()) txtComment.setError("Suggestion cannot be empty!")

            // Generate new user
            val feedback = hashMapOf(
                "comment" to txtComment.text.toString(),
                "facility" to facilityViewModel.selectedFacility.value?.id.toString(),
                "suggestion" to txtSuggestion.text.toString(),
                "type" to type.selectedItem.toString(),
            )
            db.collection("feedback").add(feedback)
        }
    }
}