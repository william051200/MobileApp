package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentAdminAllFacilityBinding
import my.tarc.mobileapp.model.Facility

class AdminAllFacilityFragment : Fragment() {
    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility images")

    // Binding
    private var _binding: FragmentAdminAllFacilityBinding? = null
    private val binding get() = _binding!!

    // Recycle view
    private lateinit var facilityList: ArrayList<Facility>
    private lateinit var recycleView: RecyclerView

    // Spinner
    private lateinit var spinner: Spinner

    private lateinit var filter: Array<String>
    private lateinit var sort: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminAllFacilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Recycle view
        recycleView = binding.adminAllFacilityRecycleView
        recycleView.layoutManager = LinearLayoutManager(this.context)
        recycleView.setHasFixedSize(true)
        facilityList = arrayListOf<Facility>()

        // Get data from firebase
        getFacilitiesFromFirebase()

        //Spinner
        spinner = binding.adminAllFacilitySpinnerSort

        // Get sorting type (default: ascending)
        sort = spinner.getItemAtPosition(0).toString()

        // Get value from sorting spinner everytime user select 1 value
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sortFacility()
                recycleView.adapter = FacilityAdapter(facilityList)
            }
        }

        // Open filter dialog
        binding.adminAllFacilityBtnFilter.setOnClickListener { openFilterDialog() }
    }

    private fun getFacilitiesFromFirebase() {
        db.collection("facility")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var facilityId = document.get("id").toString()
                    var facilityName = document.get("name").toString()
                    var facilityImage = document.get("image").toString()
                    var firstImage = facilityImage.substring(1, facilityImage.indexOf(".png"))

                    var bmp: Bitmap? = null
                    val imageReference = storageRef.child(facilityId).child("$firstImage.png")
                    val ONE_MEGABYTE: Long = 1024 * 1024

                    imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        var facility = Facility(bmp, facilityName)
                        facilityList.add(facility)
                        sortFacility()
                        recycleView.adapter = FacilityAdapter(facilityList)
                    }
                }
            }
    }

    // Update the facility list recycle view
    private fun sortFacility() {
        sort = spinner.selectedItem.toString()
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
}