package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityDetailsBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel

class FacilityDetailsFragment : Fragment() {

    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    // view model
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    // Binding
    private var _binding: FragmentFacilityDetailsBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private lateinit var images: ArrayList<Drawable>

    // Current position of selected image
    private var position = 0

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
        binding.facilityDetailRatingBar.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.e("test", v.toString())
            }
            return@OnTouchListener true
        })

        // Unfavourite this facility
        binding.facilityDetailBtnFavourite.setOnClickListener {
            binding.facilityDetailBtnUnfavourite.visibility = View.VISIBLE
            binding.facilityDetailBtnFavourite.visibility = View.INVISIBLE
            favouriteFacility()
        }

        // Favourite this facility
        binding.facilityDetailBtnUnfavourite.setOnClickListener {
            binding.facilityDetailBtnUnfavourite.visibility = View.INVISIBLE
            binding.facilityDetailBtnFavourite.visibility = View.VISIBLE
            unfavouriteFacility()
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
            findNavController().navigate(R.id.action_adminFacilityDetailFragment_to_editFacilityFragment)
        }
    }

    private fun loadFacilityDetail() {
        var id: String = facilityViewModel.selectedFacility.value!!.id
        var name: String
        var rating: String
        var address: String
        var operatingHours: String
        var feature: String

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

    private fun favouriteFacility() {

    }

    private fun unfavouriteFacility() {

    }

    // Open submit feedback dialog
    private fun submitFeedback() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_submit_feedback, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

//        val btnDelete: Button = view.findViewById(R.id.dialogDeleteFacility_btnDelete)
//        val btnCancel: Button = view.findViewById(R.id.dialogDeleteFacility_btnCancel)

//        btnCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        btnDelete.setOnClickListener {
//            deleteFacility()
//        }
    }
}