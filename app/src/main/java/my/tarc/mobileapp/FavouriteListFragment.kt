package my.tarc.mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentFavouriteListBinding

class FavouriteListFragment : Fragment() {

    private var _binding: FragmentFavouriteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateFacilityList()

        // Open sort facility dialog
        binding.favouriteListBtnSort.setOnClickListener { }

        // Open filter dialog
        binding.favouriteListBtnFilter.setOnClickListener { }

        // Navigate to user profile page
        binding.favouriteListBtnMyProfile.setOnClickListener {
            findNavController().navigate(R.id.action_favouriteListFragment_to_userProfile)
        }

    }

    // Update the facility list recycle view
    private fun updateFacilityList() {
        val facilityAdapter = FacilityAdapter()

        // need view model
//        facilityAdapter.setFacility(facilityViewModel.facilityList)
        binding.favouriteListRecycleView.adapter = facilityAdapter
    }
}