package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment
import my.tarc.mobileapp.databinding.FragmentDialogFeedbackBinding
import my.tarc.mobileapp.databinding.FragmentDialogFilterBinding

class DialogFeedbackFragment : DialogFragment() {
    // Binding
    private var _binding: FragmentDialogFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}