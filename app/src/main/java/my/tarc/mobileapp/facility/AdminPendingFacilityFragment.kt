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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentAdminPendingFacilityBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel

class AdminPendingFacilityFragment : Fragment() {

    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    // view model
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    // Binding
    private var _binding: FragmentAdminPendingFacilityBinding? = null
    private val binding get() = _binding!!

    //Store uris of picked Images
    private lateinit var images: ArrayList<Drawable>

    // Current position of selected image
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminPendingFacilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFacilityDetail()

        // Image switcher
        binding.adminPendingFacilityImageSwitcher.setFactory { ImageView(activity?.applicationContext) }

        // Store image list
        images = ArrayList()

        // next image
        binding.adminPendingFacilityBtnNextImage.setOnClickListener {
            if (position < images.size - 1) {
                position++
                binding.adminPendingFacilityImageSwitcher.setImageDrawable(images[position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images", Toast.LENGTH_SHORT).show()
            }
        }

        // previous image
        binding.adminPendingFacilityBtnPreviousImg.setOnClickListener {
            if (position > 0) {
                position--
                binding.adminPendingFacilityImageSwitcher.setImageDrawable(images!![position])
            } else {
                //Display no more image
                Toast.makeText(activity, "No more images", Toast.LENGTH_SHORT).show()
            }
        }

        // Approve facility
        binding.adminPendingFacilityBtnApprove.setOnClickListener { approveFacility() }

        // Deny facility
        binding.adminPendingFacilityBtnDeny.setOnClickListener { denyFacility() }
    }

    private fun loadFacilityDetail() {
        var id: String = facilityViewModel.selectedFacility.value!!.id
        var name: String
        var address: String
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

            binding.adminPendingFacilityTxtFacilityName.text = name
            binding.adminPendingFacilityTxtFacilityAddress.text = address
            binding.adminPendingFacilityTxtOperatingHours.text = operatingHours
            binding.adminPendingFacilityTxtFacilityFeatures.text = feature
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
                    binding.adminPendingFacilityImageSwitcher.setImageDrawable(images[0])
                }
            }
        }
    }

    private fun approveFacility() {
        var id: String = facilityViewModel.selectedFacility.value!!.id

        db.collection("facility").document(id).update("status", "Approved")
        Toast.makeText(context, "Approved facility", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_adminPendingFacilityFragment_to_facilityListFragment)
    }

    private fun denyFacility() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_deny_facility, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val btnDeny: Button = view.findViewById(R.id.dialogDeclineFacility_btnDeny)
        val btnCancel: Button = view.findViewById(R.id.dialogDeclineFacility_btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDeny.setOnClickListener {
            var id: String = facilityViewModel.selectedFacility.value!!.id

            db.collection("facility").document(id).delete().addOnSuccessListener {
                storageRef.child(id).delete().addOnSuccessListener {
                    Toast.makeText(context, "Denied facility", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_adminPendingFacilityFragment_to_facilityListFragment)
                }
            }
        }
    }

}