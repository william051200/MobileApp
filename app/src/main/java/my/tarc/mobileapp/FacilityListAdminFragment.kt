package my.tarc.mobileapp

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.databinding.FragmentFacilityListAdminBinding

class FacilityListAdminFragment : Fragment() {
    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility images")

    // Binding
    private var _binding: FragmentFacilityListAdminBinding? = null
    private val binding get() = _binding!!

    // Private lateinit var filter: Array<String>
    private lateinit var facilityList: ArrayList<Facility>
    private lateinit var sort: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityListAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.facilityListAdminRecycleView.layoutManager = LinearLayoutManager(this.context)
        binding.facilityListAdminRecycleView.setHasFixedSize(true)
        facilityList = arrayListOf<Facility>()
        var type = ""

        getFacilitiesFromFirebase(type)

        sort = binding.facilityListAdminSpinnerSort.getItemAtPosition(0).toString()

        // Get value from sorting spinner everytime user select 1 value
        binding.facilityListAdminSpinnerSort.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    sort = parent?.getItemAtPosition(position).toString()
                    sortFacility()
                    binding.facilityListAdminRecycleView.adapter = FacilityAdapter(facilityList)
                }
            }

        // Open filter dialog
        binding.facilityListAdminBtnFilter.setOnClickListener { openFilterDialog() }
    }

    private fun getFacilitiesFromFirebase(type: String) {
        if (type == "Pending") {
            db.collection("facility")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        var facilityId = document.get("id").toString()
                        var facilityName = document.get("name").toString()
                        var facilityImage = document.get("image").toString()
                        var firstImage = facilityImage.substring(1, facilityImage.indexOf(".png"))
                        var facilityImageBmp = getImage(facilityId, "$firstImage.png")
                        var facility = Facility(facilityImageBmp, facilityName)
                        facilityList.add(facility)
                    }
                    binding.facilityListAdminRecycleView.adapter = FacilityAdapter(facilityList)
                }
        } else {
            db.collection("facility")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        var facilityId = document.get("id").toString()
                        var facilityName = document.get("name").toString()
                        var facilityImage = document.get("image").toString()
                        var firstImage = facilityImage.substring(1, facilityImage.indexOf(".png"))
                        var facilityImageBmp = getImage(facilityId, "$firstImage.png")
                        var facility = Facility(facilityImageBmp, facilityName)
                        facilityList.add(facility)
                    }
                    binding.facilityListAdminRecycleView.adapter = FacilityAdapter(facilityList)
                }
        }
    }

    // Update the facility list recycle view
    private fun sortFacility() {
        if (sort == "Sort ascending") {
            facilityList.sortBy { it.name }
        } else if (sort == "Sort descending") {
            facilityList.sortByDescending { it.name }
        }
    }

    private fun openFilterDialog() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_filter, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val btnOk: Button = view.findViewById(R.id.filterDialog_btnApply)
        btnOk.setOnClickListener {
            // get filter value in array
            dialog.dismiss()
        }
    }

    private fun getImage(facilityId: String, imageName: String): Bitmap? {
        var bmp: Bitmap? = null
        val imageReference = storageRef.child(facilityId).child(imageName)
        val ONE_MEGABYTE: Long = 1024 * 1024

        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        return bmp
    }
}