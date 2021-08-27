package my.tarc.mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import my.tarc.mobileapp.databinding.FragmentFacilityListUserBinding

class FacilityListUserFragment : Fragment() {

    private var _binding: FragmentFacilityListUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilityListUserBinding.inflate(inflater, container, false)
        return binding.root
    }
}