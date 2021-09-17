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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityListBinding
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.viewmodel.FacilityViewModel
import my.tarc.mobileapp.viewmodel.UserViewModel

class FacilityListFragment : Fragment() {
    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    // Get active user
    private val userViewModel: UserViewModel by activityViewModels()
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    // Binding
    private var _binding: FragmentFacilityListBinding? = null
    private val binding get() = _binding!!

    // Recycle view
    private lateinit var collectedFacilityList: ArrayList<Facility>
    private lateinit var facilityList: ArrayList<Facility>
    private lateinit var recyclerView: RecyclerView

    private lateinit var spinner: Spinner
    private lateinit var sort: String
    private lateinit var filterLocation: String
    private lateinit var filterCategory: String
    private var query: Query? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set toolbar title dynamically
        //activity?.title = facilityViewModel.toolBarTitle.value

        // Set bottom tab's visibility dynamically
        if (userViewModel.activeUser.value?.userType == "admin") {
            binding.facilityListLinearLayout2.visibility = View.INVISIBLE
        }

        // Recycle view
        recyclerView = binding.facilityListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        facilityList = arrayListOf()
        collectedFacilityList = arrayListOf()

        // Get data from firebase
        getFacilitiesFromFirebase()

        //Spinner
        spinner = binding.facilityListSpinnerSort

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
                recyclerView.adapter = FacilityAdapter(facilityList) { facility ->
                    // Pass selected facility to facility_details
                    facilityViewModel.setFacility(facility)
                    if (userViewModel.activeUser.value!!.userType == "user")
                        findNavController().navigate(R.id.action_facilityListFragment_to_facilityDetailsFragment)
                    else if (facilityViewModel.toolBarTitle.value == "Facility List")
                        findNavController().navigate(R.id.action_facilityListFragment_to_adminFacilityDetailFragment)
                    else
                        findNavController().navigate(R.id.action_facilityListFragment_to_adminPendingFacilityFragment)
                }
            }
        }

        // Open filter dialog
        binding.facilityListBtnFilter.setOnClickListener { openFilterDialog() }

        // Navigate to user profile page
        binding.facilityListBtnMyProfile.setOnClickListener {
            findNavController().navigate(R.id.action_facilityListFragment_to_userProfile)
        }
    }

    private fun getFacilitiesFromFirebase() {
        // Get different facilities based on user type
        if (userViewModel.activeUser.value?.userType == "user") {
            var facilityType = facilityViewModel.selectedFacilityType.value
            var tempFavouriteList: ArrayList<String>? = null

            if (facilityType == "favourite") {
                var favouriteList:
                        MutableList<String>? = null

                // Get user's favorite facility's id
                db.collection("user").document(userViewModel.activeUser.value!!.email).get()
                    .addOnSuccessListener {
                        tempFavouriteList = it.get("favourite_facility") as ArrayList<String>
                        if (tempFavouriteList!!.isNotEmpty()) {
                            favouriteList = (tempFavouriteList)?.toMutableList()
                            binding.facilityListTxtNoData.visibility = View.INVISIBLE
                            query = db.collection("facility")
                                .whereEqualTo("status", "Approved")
                            // Query that retrieves user's favorite facilities
                            for (favourite in favouriteList!!) {
                                db.collection("facility").document(favourite).get()
                                    .addOnSuccessListener { it ->
                                        var facilityId = it.id
                                        var facilityName = it.get("name").toString()
                                        var facilityState = it.get("address_state").toString()

                                        var bmp: Bitmap? = null
                                        val imageReference =
                                            storageRef.child(facilityId).child("0.png")
                                        val ONE_MEGABYTE: Long = 1024 * 1024

                                        imageReference.getBytes(ONE_MEGABYTE)
                                            .addOnSuccessListener { bytes ->
                                                bmp = BitmapFactory.decodeByteArray(
                                                    bytes,
                                                    0,
                                                    bytes.size
                                                )
                                                var facility = Facility(
                                                    bmp,
                                                    facilityName,
                                                    facilityState,
                                                    facilityId
                                                )
                                                collectedFacilityList.add(facility)
                                                facilityList.add(facility)
                                                sortFacility()
                                                recyclerView.adapter =
                                                    FacilityAdapter(facilityList) { facility ->
                                                        // Pass selected facility to facility_details
                                                        facilityViewModel.setFacility(facility)
                                                        if (userViewModel.activeUser.value!!.userType == "user")
                                                            findNavController().navigate(R.id.action_facilityListFragment_to_facilityDetailsFragment)
                                                        else if (facilityViewModel.toolBarTitle.value == "Facility List")
                                                            findNavController().navigate(R.id.action_facilityListFragment_to_adminFacilityDetailFragment)
                                                        else
                                                            findNavController().navigate(R.id.action_facilityListFragment_to_adminPendingFacilityFragment)
                                                    }
                                            }

                                    }
                            }

                        } else {
                            binding.facilityListTxtNoData.visibility = View.VISIBLE
                            binding.facilityListTxtNoData.text = "No facility"
                        }
                    }


            } else {
                query = db.collection("facility")
                    .whereEqualTo("category", facilityType)
                populateAdapter()
            }

        } else {
            if (facilityViewModel.toolBarTitle.value == "Facility List") {
                query = db.collection("facility")
                    .whereEqualTo("status", "Approved")
            } else { // Get pending facilities
                query = db.collection("facility")
                    .whereEqualTo("status", "Pending")
            }

            populateAdapter()
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
        var txtCategory: TextView = view.findViewById(R.id.filterDialog_txtCategory)

        if (userViewModel.activeUser.value!!.userType == "user" && facilityViewModel.selectedFacilityType.value != "favourite") {
            spinnerCategory.visibility = View.INVISIBLE
            txtCategory.visibility = View.INVISIBLE
        }

        btnOk.setOnClickListener {
            if (!(userViewModel.activeUser.value!!.userType == "user" && facilityViewModel.selectedFacilityType.value != "favourite")) {
                filterCategory = spinnerCategory.selectedItem.toString()
            } else filterCategory = "All"
            filterLocation = spinnerLocation.selectedItem.toString()
            filterFacility()
            recyclerView.adapter = FacilityAdapter(facilityList) { facility ->
                // Pass selected facility to facility_details
                facilityViewModel.setFacility(facility)
                if (userViewModel.activeUser.value!!.userType == "user")
                    findNavController().navigate(R.id.action_facilityListFragment_to_facilityDetailsFragment)
                else if (facilityViewModel.toolBarTitle.value == "Facility List")
                    findNavController().navigate(R.id.action_facilityListFragment_to_adminFacilityDetailFragment)
                else
                    findNavController().navigate(R.id.action_facilityListFragment_to_adminPendingFacilityFragment)
            }
            dialog.dismiss()
        }
    }

    private fun populateAdapter() {
        facilityList.clear()
        query?.get()?.addOnSuccessListener { documents ->
            if (documents.size() < 1) {
                binding.facilityListTxtNoData.visibility = View.VISIBLE
                binding.facilityListTxtNoData.text = "No facility"
            } else binding.facilityListTxtNoData.visibility = View.INVISIBLE

            for (document in documents) {
                var facilityId = document.id
                var facilityName = document.get("name").toString()
                var facilityState = document.get("address_state").toString()

                //TODO(CHANGE TO USING MAP FIELD)
                //                    val address = document.get("address") as Map<String, *>
                //                    val facilityState = address["state"] as String

                var bmp: Bitmap? = null
                val imageReference = storageRef.child(facilityId).child("0.png")
                val ONE_MEGABYTE: Long = 1024 * 1024

                imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    var facility = Facility(bmp, facilityName, facilityState, facilityId)
                    collectedFacilityList.add(facility)
                    facilityList.add(facility)
                    sortFacility()
                    recyclerView.adapter = FacilityAdapter(facilityList) { facility ->
                        // Pass selected facility to facility_details
                        facilityViewModel.setFacility(facility)
                        if (userViewModel.activeUser.value!!.userType == "user")
                            findNavController().navigate(R.id.action_facilityListFragment_to_facilityDetailsFragment)
                        else if (facilityViewModel.toolBarTitle.value == "Facility List")
                            findNavController().navigate(R.id.action_facilityListFragment_to_adminFacilityDetailFragment)
                        else
                            findNavController().navigate(R.id.action_facilityListFragment_to_adminPendingFacilityFragment)
                    }
                }
            }
        }
    }

}