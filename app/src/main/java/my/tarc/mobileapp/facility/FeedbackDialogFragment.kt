package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle

import androidx.fragment.app.DialogFragment

class FeedbackDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
//            builder.setView(inflater.inflate(R.layout.dialog_signin, null))
//                // Add action buttons
//                .setPositiveButton(R.string.signin,
//                    DialogInterface.OnClickListener { dialog, id ->
//                        // sign in the user ...
//                    })
//                .setNegativeButton(R.string.cancel,
//                    DialogInterface.OnClickListener { dialog, id ->
//                        getDialog().cancel()
//                    })
            builder.create()
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}