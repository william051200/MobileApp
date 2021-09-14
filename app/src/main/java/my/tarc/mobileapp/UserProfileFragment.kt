package my.tarc.mobileapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.databinding.FragmentUserProfileBinding


class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Facility Category
        binding.userProfileBtnFacilityCategory.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

        // Navigate to Facility Category
        binding.btnProfileCancel.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }

        //Navigate to Facility Category
        binding.btnProfileSave.setOnClickListener {
            findNavController().navigate(R.id.action_userProfile_to_facilityCategory)
        }
    }
}