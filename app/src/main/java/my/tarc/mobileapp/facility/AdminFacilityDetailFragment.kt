package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        // Need images
        // Set features to null if nothing

        var id: String = "dummy1"
        var name: String
        var rating: Long
        var address: String
        var operatingHours: String
        var feature: String

        db.collection("facility").document(id).get()
            .addOnSuccessListener {
                Log.e("test", it.get("rating").toString())
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
                binding.adminFacilityDetailRatingCount.text = rating.toString()
                binding.adminFacilityDetailTxtFacilityAddress.text = address
                binding.adminFacilityDetailTxtOperatingHours.text = operatingHours
                binding.adminFacilityDetailTxtFacilityFeatures.text = feature
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