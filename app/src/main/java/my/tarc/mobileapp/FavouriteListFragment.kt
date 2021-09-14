package my.tarc.mobileapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentFavouriteListBinding

class FavouriteListFragment : Fragment() {

//    private var _binding: FragmentFavouriteListBinding? = null
//    private val binding get() = _binding!!

//    private var sort: Int = binding.favouriteListSpinnerSort.selectedItemPosition
    private lateinit var filter: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_list, container, false)
//        _binding = FragmentFavouriteListBinding.inflate(inflater, container, false)
//        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("Test", "test")
        super.onCreate(savedInstanceState)
        updateFacilityList()

        // Open filter dialog
//        binding.favouriteListBtnFilter.setOnClickListener { openFilterDialog() }

        // Navigate to user profile page
//        binding.favouriteListBtnMyProfile.setOnClickListener {
//            findNavController().navigate(R.id.action_favouriteListFragment_to_userProfile)
//        }

    }

    // Update the facility list recycle view
    private fun updateFacilityList() {
        val facilityAdapter = FacilityAdapter()

//        if (sort == 0) {
//            // Sort ascending
//        } else if (sort == 1) {
//            // Sort descending
//        } else if (sort == 2) {
//            // Sort nearest
//        } else if (sort == 3) {
//            // Sort furthest
//        }


        // need view model
//        facilityAdapter.setFacility(facilityViewModel.facilityList)
//        binding.favouriteListRecycleView.adapter = facilityAdapter
    }

    private fun openFilterDialog() {
        val view = View.inflate(this.context, R.layout.fragment_dialog_filter, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnOk: Button = view.findViewById(R.id.filterDialog_btnApply)
        btnOk.setOnClickListener {
            // get filter value in array
            dialog.dismiss()
        }
    }
}