package my.tarc.mobileapp.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import my.tarc.mobileapp.databinding.FragmentFeedbackListBinding

class FeedbackListFragment : Fragment() {
    // Binding
    private var _binding: FragmentFeedbackListBinding? = null
    private val binding get() = _binding!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}