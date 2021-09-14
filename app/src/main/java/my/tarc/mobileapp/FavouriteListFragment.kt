package my.tarc.mobileapp

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.databinding.FragmentFavouriteListBinding


class FavouriteListFragment : Fragment() {

    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility photos")

    // Binding
    private var _binding: FragmentFavouriteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var sort: String
    //    private lateinit var filter: Array<String>
    private lateinit var facilityList: ArrayList<Facility>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        facilityList = arrayListOf<Facility>()

        db.collection("facility")
            .whereEqualTo("status", "Approved")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var facilityId = document.get("id").toString()
                    var facilityName = document.get("name").toString()
                    var facilityPicture = document.get("picture").toString()
                    var firstPicture = facilityPicture.substring(1, facilityPicture.indexOf(","))
                    var facility = Facility(getImage(facilityId, firstPicture), facilityName)
                    facilityList.add(facility)
                }

                binding.favouriteListRecycleView.adapter = FacilityAdapter(facilityList)
            }

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
                    sort = parent?.getItemAtPosition(position).toString()
                }
            }

        // Open filter dialog
        binding.favouriteListBtnFilter.setOnClickListener { openFilterDialog() }

        // Navigate to user profile page
        binding.favouriteListBtnMyProfile.setOnClickListener {
            findNavController().navigate(R.id.action_favouriteListFragment_to_userProfile)
        }
    }

    // Update the facility list recycle view
    private fun updateFacilityList() {
        if (sort == "Sort ascending") {

        } else if (sort == "Sort descending") {

        } else if (sort == "Sort nearest") {

        } else if (sort == "Sort furthest") {

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
        val imageReference = storageRef.child(facilityId).child(imageName)
        val ONE_MEGABYTE: Long = 1024 * 1024
        var bmp: Bitmap? = null

        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        return bmp
    }
}