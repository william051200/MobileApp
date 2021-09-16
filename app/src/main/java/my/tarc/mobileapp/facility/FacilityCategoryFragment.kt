package my.tarc.mobileapp.facility

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFacilityCategoryBinding
import my.tarc.mobileapp.viewmodel.UserViewModel

class FacilityCategoryFragment : Fragment() {

    // Binding
    private var _binding: FragmentFacilityCategoryBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("user", userViewModel.activeUser.value!!.email)
        Log.e("user", userViewModel.activeUser.value!!.name)
        Log.e("user", userViewModel.activeUser.value!!.password)
        Log.e("user", userViewModel.activeUser.value!!.favoriteFacilities.size.toString())
        Log.e("user", userViewModel.activeUser.value!!.userType)

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