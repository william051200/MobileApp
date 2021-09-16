package my.tarc.mobileapp.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityCategoryBinding

class FacilityCategoryFragment : Fragment() {

    // Binding
    private var _binding: FragmentFacilityCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to toilet page
        binding.btnFavoriteCategory.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to parking page
        binding.btnFavoriteCategory.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to seat page
        binding.btnFavoriteCategory.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to wheelchair page
        binding.btnFavoriteCategory.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to favourite category page
        binding.btnFavoriteCategory.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_facilityListFragment)
        }

        // Navigate to User Profile
        binding.facilityCategoryBtnMyProfile.setOnClickListener {
            findNavController().navigate(R.id.action_facilityCategory_to_userProfile)
        }
    }
}