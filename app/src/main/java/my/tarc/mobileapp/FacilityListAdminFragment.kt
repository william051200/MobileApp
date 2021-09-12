package my.tarc.mobileapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentFacilityListAdminBinding

class FacilityListAdminFragment : Fragment() {

    private var _binding: FragmentFacilityListAdminBinding? = null
    private val binding get() = _binding!!

    private var sort: Int = binding.facilityListAdminSpinnerSort.selectedItemPosition
    private lateinit var filter: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityListAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateFacilityList()

        // Open filter dialog
        binding.facilityListAdminBtnFilter.setOnClickListener { openFilterDialog() }

    }

    // Update the facility list recycle view
    private fun updateFacilityList() {
        val facilityAdapter = FacilityAdapter()

        if (sort == 0) {
            // Sort ascending
        } else if (sort == 1) {
            // Sort descending
        } else if (sort == 2) {
            // Sort nearest
        } else if (sort == 3) {
            // Sort furthest
        }


        // need view model
//        facilityAdapter.setFacility(facilityViewModel.facilityList)
        binding.facilityListAdminRecycleView.adapter = facilityAdapter
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