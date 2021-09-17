package my.tarc.mobileapp.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityCategoryBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel

class FacilityCategoryFragment : Fragment() {

    // Binding
    private var _binding: FragmentFacilityCategoryBinding? = null
    private val binding get() = _binding!!

    // View model
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityCategoryBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Facility Category"
               return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to toilet page
        binding.facilityCategoryBtnToilet.setOnClickListener {
            facilityViewModel.setFacilityType("OKU Toilet")
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to parking page
        binding.facilityCategoryBtnParking.setOnClickListener {
            facilityViewModel.setFacilityType("OKU Parking")
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to park page
        binding.facilityCategoryBtnPark.setOnClickListener {
            facilityViewModel.setFacilityType("OKU Park")
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to bus station page
        binding.facilityCategoryBtnBusStation.setOnClickListener {
            facilityViewModel.setFacilityType("OKU Bus")
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to favourite category page
        binding.facilityCategoryBtnFavourite.setOnClickListener {
            facilityViewModel.setFacilityType("favourite")
            facilityViewModel.setTitle("Favourite List")
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to User Profile
        binding.facilityCategoryBtnMyProfile.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_userProfile)
        }
    }
}