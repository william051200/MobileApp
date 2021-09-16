package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFeedbackListBinding
import my.tarc.mobileapp.model.Address
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.Feedback
import org.w3c.dom.Text

class FeedbackListFragment : Fragment() {
    // Firestore database
    private val db = Firebase.firestore

    // Binding
    private var _binding: FragmentFeedbackListBinding? = null
    private val binding get() = _binding!!

    // Recycler view
    private lateinit var feedbackList: ArrayList<Feedback>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Recycler view
        recyclerView = binding.feedbackListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        feedbackList = arrayListOf()

        // Get data from firebase
        getFeedbacksFromFirebase()
    }

    private fun getFeedbacksFromFirebase() {
        db.collection("feedback").get().addOnSuccessListener { documents ->
            for (document in documents) {
                var feedbackType = document.get("type").toString()
                var feedbackId = document.id
                var facility = document.get("facility").toString()
                var comment = document.get("comment").toString()
                var suggestion = document.get("suggestion").toString()
                var feedback = Feedback(comment, feedbackId, facility, feedbackType, suggestion)
                feedbackList.add(feedback)
            }
            recyclerView.adapter = FeedbackAdapter(feedbackList) { feedback ->
                // Prompt dialog with the selected feedback's details
                openFeedbackDialog(feedback)
            }
            binding.feedbackListFeedbackCount.text = "Total feedbacks (${feedbackList.size})"
        }
    }

    // Open filter dialog and get filter category or location
    private fun openFeedbackDialog(feedback: Feedback) {
        val view = View.inflate(this.context, R.layout.fragment_dialog_feedback, null)

        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)

        val dialog = builder.create()

        // Populate the feedback dialog with the selected feedback's details
        dialog.setTitle(feedback.type)
        val textViewComment: TextView = view.findViewById(R.id.dialogFeedback_txtFeedbackComment)
        val textViewSuggestion: TextView = view.findViewById(R.id.dialogFeedback_txtSuggestion)
        val textViewFacilityName: TextView = view.findViewById(R.id.feedback_txtFacilityName)
        val textViewCurrentInfo: TextView = view.findViewById(R.id.dialogFeedback_txtCurrentInfo)
        val btnAccept: Button = view.findViewById(R.id.dialogFeedback_btnAccept)
        val btnDelete: Button = view.findViewById(R.id.dialogFeedback_btnDelete)

        textViewComment.text = feedback.comment
        textViewSuggestion.text = feedback.suggestion

        var facility = Facility(feedback.facility)

        // Get the associated facility
        db.collection("facility").document(feedback.facility).get().addOnSuccessListener {
            textViewFacilityName.text = facility.name

            // Populate current_info based on the feedback's type
            when (feedback.type) {
                "Incorrect Name" -> textViewCurrentInfo.text = it.get("name").toString()
                "Incorrect Operation Hours" -> {
                    // Operating hours
                    var startTime: String = it.get("starting_hour") as String
                    var closeTime: String = it.get("closing_hour") as String
                    var operatingHours = "$startTime - $closeTime"
                    textViewCurrentInfo.text = operatingHours
                }
                "Incorrect Address" -> {
                    // Address
                    var street: String = it.get("address_street") as String
                    var postcode: String = it.get("address_postcode") as String
                    var city: String = it.get("address_city") as String
                    var state: String = it.get("address_state") as String
                    var address = "$street, $postcode $city, $state"
                    textViewCurrentInfo.text = address
                }
                "Incorrect Oku Feature" -> textViewCurrentInfo.text =
                    it.get("oku_feature").toString()
            }
        }

        dialog.show()

        //TODO(ACCEPT BUTTON IN FEEDBACK DETAILS)
        btnAccept.setOnClickListener {

            // Update the selected facility's
            val facilityRef = db.collection("facility").document(feedback.facility)
            when (feedback.type) {
                "Incorrect Name" -> {
                    facilityRef.update("name", feedback.suggestion)
                    Toast.makeText(
                        context,
                        "Facility Name Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                "Incorrect Operation Hours" -> {
                    // Operating hours
                    var trimmedSuggestion = feedback.suggestion.trim()
                    var startTime: String =
                        trimmedSuggestion.substring(0, feedback.suggestion.indexOf("-"))
                    var closeTime: String =
                        trimmedSuggestion.substring(
                            feedback.suggestion.indexOf("-") + 1,
                            trimmedSuggestion.length
                        )

                    facilityRef.update("starting_hour", startTime)
                    facilityRef.update("closing_hour", closeTime)
                    Toast.makeText(
                        context,
                        "Facility Operating Hours Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                "Incorrect Address" -> {
                    var arrayStr = feedback.suggestion.split(",").toTypedArray()
                    // Address
                    var street: String = arrayStr[0]
                    var postcode: String = arrayStr[1]
                    var city: String = arrayStr[2]
                    var state: String = arrayStr[3]

                    facilityRef.update("address_city", city)
                    facilityRef.update("address_postcode", postcode)
                    facilityRef.update("address_state", state)
                    facilityRef.update("address_street", street)
                    Toast.makeText(
                        context,
                        "Facility Address Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                "Incorrect Oku Feature" -> {
                    var suggestion = feedback.suggestion
                    if (suggestion == "None" || suggestion == "OKU Parking" || suggestion == "OKU Toilet" || suggestion == "OKU Seat" || suggestion == "Wheelchair") {
                        facilityRef.update("oku_feature", feedback.suggestion)
                        Toast.makeText(
                            context,
                            "Facility OKU Feature Updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Invalid OKU Feature, Update Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            dialog.dismiss()
        }

        //TODO(CHANGE DOCUMENT ID)
        btnDelete.setOnClickListener {
            // Update the selected facility's
            db.collection("feedback").document(feedback.id).delete().addOnSuccessListener {
                Toast.makeText(context, "Feedback Deleted", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
    }

}