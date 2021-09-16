package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentAdminFacilityDetailBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel


class AdminFacilityDetailFragment : Fragment() {

    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    // view model
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    // Binding
    private var _binding: FragmentAdminFacilityDetailBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private lateinit var images: ArrayList<Drawable>

    // Current position of selected image
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminFacilityDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFacilityDetail()

        // Image switcher
        binding.adminFacilityDetailImageSwitcher.setFactory { ImageView(activity?.applicationContext) }

        // Store image list
        images = ArrayList()

        // next image
        binding.adminFacilityDetailBtnNextImage.setOnClickListener {
            if (position < images.size - 1) {
                position++
                binding.adminFacilityDetailImageSwitcher.setImageDrawable(images[position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images", Toast.LENGTH_SHORT).show()
            }
        }

        // previous image
        binding.adminFacilityDetailBtnPreviousImg.setOnClickListener {
            if (position > 0) {
                position--
                binding.adminFacilityDetailImageSwitcher.setImageDrawable(images!![position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images", Toast.LENGTH_SHORT).show()
            }
        }

        // Validate if this facility has feedback then show feedback button

        binding.adminFacilityDetailBtnViewfeedback.setOnClickListener {
            // view feedback
        }

        binding.adminFacilityDetailBtnEdit.setOnClickListener {
            // navigate to where?
        }

        binding.adminFacilityDetailBtnDelete.setOnClickListener {
            openDeleteFacilityDialog()
        }
    }

    private fun loadFacilityDetail() {
        // Need feedback
        // Set features to null if nothing
        var id: String = facilityViewModel.selectedFacility.value!!.id
        var name: String
        var rating: Long
        var address: String
        var operatingHours: String
        var feature: String

        db.collection("facility").document(id).get()
            .addOnSuccessListener {
                name = it.get("name") as String
                rating = it.get("rating") as Long
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

                binding.adminFacilityDetailTxtFacilityName.text = name
                binding.adminFacilityDetailRatingBar.rating = rating.toFloat()
                binding.adminFacilityDetailRatingCount.text = "(${rating})"
                binding.adminFacilityDetailTxtFacilityAddress.text = address
                binding.adminFacilityDetailTxtOperatingHours.text = operatingHours
                binding.adminFacilityDetailTxtFacilityFeatures.text = feature
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
                    binding.adminFacilityDetailImageSwitcher.setImageDrawable(images[0])
                }
            }
        }
    }

    // Open delete facility dialog
    private fun openDeleteFacilityDialog() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_filter, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val btnDelete: Button = view.findViewById(R.id.dialogDeleteFacility_btnDelete)
        val btnCancel: Button = view.findViewById(R.id.dialogDeleteFacility_btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            // Delete facility and go back to last page
        }
    }
}