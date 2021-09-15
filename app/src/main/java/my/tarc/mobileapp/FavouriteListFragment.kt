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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private lateinit var facilityList: ArrayList<Facility>
    private lateinit var recycleView: RecyclerView

    private lateinit var filter: Array<String>
    private lateinit var sort: String

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
        getFacilitiesFromFirebase()

        // Get sorting type (default: ascending)
        sort = binding.favouriteListSpinnerSort.getItemAtPosition(0).toString()

        // Get value from sorting spinner everytime user select 1 value
        binding.favouriteListSpinnerSort.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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

                    var bmp: Bitmap? = null
                    val imageReference = storageRef.child(facilityId).child("0.png")
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
        sort = binding.favouriteListSpinnerSort.selectedItem.toString()
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