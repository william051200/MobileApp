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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFavouriteListBinding

class FavouriteListFragment : Fragment() {
    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    // Binding
    private var _binding: FragmentFavouriteListBinding? = null
    private val binding get() = _binding!!

    // Recycle view
    private lateinit var collectedFacilityList: ArrayList<Facility>
    private lateinit var facilityList: ArrayList<Facility>
    private lateinit var recycleView: RecyclerView

    private lateinit var spinner: Spinner
    private lateinit var sort: String
    private lateinit var filterLocation: String
    private lateinit var filterCategory: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Recycle view
        recycleView = binding.favouriteListRecycleView
        recycleView.layoutManager = LinearLayoutManager(this.context)
        recycleView.setHasFixedSize(true)
        facilityList = arrayListOf<Facility>()
        collectedFacilityList = arrayListOf<Facility>()

        // Get data from firebase
        getFacilitiesFromFirebase()

        //Spinner
        spinner = binding.favouriteListSpinnerSort

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
        binding.favouriteListBtnFilter.setOnClickListener { openFilterDialog() }

        // Navigate to user profile page
        binding.favouriteListBtnMyProfile.setOnClickListener {
            findNavController().navigate(R.id.action_favouriteListFragment_to_userProfile)
        }
    }

    private fun getFacilitiesFromFirebase() {
        db.collection("facility")
            .whereEqualTo("status", "Approved")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var facilityId = document.get("id").toString()
                    var facilityName = document.get("name").toString()
                    var facilityState = document.get("address_state").toString()

                    var bmp: Bitmap? = null
                    val imageReference = storageRef.child(facilityId).child("0.png")
                    val ONE_MEGABYTE: Long = 1024 * 1024

                    imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        var facility = Facility(bmp, facilityName, facilityState)
                        collectedFacilityList.add(facility)
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

    // Sort facility according to state
    private fun filterFacility() {
        var filteredCategory: ArrayList<Facility> = arrayListOf()
        var finalFilterList: ArrayList<Facility> = arrayListOf()

        // Filter facility according to category
        if (filterCategory != "All") {
            collectedFacilityList.map {
                if (it.category == filterCategory) filteredCategory.add(it)
            }
        } else filteredCategory = collectedFacilityList

        // Filter facility according to location
        if (filterLocation != "All") {
            filteredCategory.map {
                if (it.address.state == filterLocation) finalFilterList.add(it)
            }
        } else finalFilterList = filteredCategory

        facilityList = finalFilterList
        sortFacility()
    }

    // Open filter dialog and get filter category or location
    private fun openFilterDialog() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_filter, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val btnOk: Button = view.findViewById(R.id.filterDialog_btnApply)
        val spinnerCategory: Spinner = view.findViewById(R.id.filterDialog_spinnerCategory)
        val spinnerLocation: Spinner = view.findViewById(R.id.filterDialog_spinnerLocation)
        btnOk.setOnClickListener {
            filterCategory = spinnerCategory.selectedItem.toString()
            filterLocation = spinnerLocation.selectedItem.toString()
            filterFacility()
            recycleView.adapter = FacilityAdapter(facilityList)
            dialog.dismiss()
        }
    }
}