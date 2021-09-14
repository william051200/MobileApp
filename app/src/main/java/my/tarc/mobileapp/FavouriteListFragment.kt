package my.tarc.mobileapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentFavouriteListBinding


class FavouriteListFragment : Fragment() {

    private var _binding: FragmentFavouriteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var sort: String
    private lateinit var filter: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reset()
        updateFacilityList()

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
                    updateFacilityList()
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
        val facilityAdapter = FacilityAdapter()

        if (sort == "Sort ascending") {

        } else if (sort == "Sort descending") {

        } else if (sort == "Sort nearest") {

        } else if (sort == "Sort furthest") {

        }

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

        val btnOk: Button = view.findViewById(R.id.filterDialog_btnApply)
        btnOk.setOnClickListener {
            // get filter value in array
            dialog.dismiss()
        }
    }

    private fun reset() {
        binding.favouriteListSpinnerSort.setSelection(0)
        sort = binding.favouriteListSpinnerSort.getItemAtPosition(0).toString()
    }
}