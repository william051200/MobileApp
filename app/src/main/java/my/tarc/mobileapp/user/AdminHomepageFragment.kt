package my.tarc.mobileapp.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentAdminHomepageBinding
import my.tarc.mobileapp.viewmodel.FacilityViewModel

class AdminHomepageFragment : Fragment() {
    private var _binding: FragmentAdminHomepageBinding? = null
    private val binding get() = _binding!!
    private val facilityViewModel: FacilityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animationFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.slideup1)
        val animationFadeIn1 = AnimationUtils.loadAnimation(getContext(), R.anim.slideup2)
        val animationFadeIn2 = AnimationUtils.loadAnimation(getContext(), R.anim.slideup3)
        val animationFadeIn3 = AnimationUtils.loadAnimation(getContext(), R.anim.slideup4)
        val animationFadeIn4 = AnimationUtils.loadAnimation(getContext(), R.anim.slideup5)
        val btnAllFacility = binding.adminHomeBtnAllFacility
        val btnPendingApprovals = binding.adminHomeBtnPendingApprovals
        val btnFeedbackList = binding.adminHomeBtnFeedbackList

//        binding.hi.startAnimation(animationFadeIn)
//        binding.actiontext.startAnimation(animationFadeIn1)
//        btnAllFacility.startAnimation(animationFadeIn2)
//        btnPendingApprovals.startAnimation(animationFadeIn3)
//        btnFeedbackList.startAnimation(animationFadeIn4)

        // Navigate to all facility page
        btnAllFacility.setOnClickListener {
            facilityViewModel.setTitle("Facility List")
            findNavController().navigate(R.id.action_adminHomepageFragment_to_facilityListFragment)
        }

        // Navigate to pending approvals page
        btnPendingApprovals.setOnClickListener {
            facilityViewModel.setTitle("Pending Approvals")
            findNavController().navigate(R.id.action_adminHomepageFragment_to_facilityListFragment)
        }

        // Navigate to feedback list page
        btnFeedbackList.setOnClickListener {
            facilityViewModel.setTitle("Feedback List")
            findNavController().navigate(R.id.action_adminHomepageFragment_to_feedbackListFragment)
        }
    }
}