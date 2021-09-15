package my.tarc.mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import my.tarc.mobileapp.databinding.FragmentAdminHomepageBinding

class AdminHomepageFragment : Fragment() {
    private var _binding: FragmentAdminHomepageBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        binding.hi.startAnimation(animationFadeIn)
        binding.actiontext.startAnimation(animationFadeIn1)
        binding.activity1.startAnimation(animationFadeIn2)
        binding.activity2.startAnimation(animationFadeIn3)
        binding.activity3.startAnimation(animationFadeIn4)
    }
}