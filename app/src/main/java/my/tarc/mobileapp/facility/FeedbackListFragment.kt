package my.tarc.mobileapp.facility

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.tarc.mobileapp.R
import my.tarc.mobileapp.databinding.FragmentFeedbackListBinding
import my.tarc.mobileapp.model.Feedback

class FeedbackListFragment : Fragment() {
    // Firestore database
    private val db = Firebase.firestore

    // Binding
    private var _binding: FragmentFeedbackListBinding? = null
    private val binding get() = _binding!!

    // Recycler view
    private lateinit var feedbackList: ArrayList<Feedback>
    private lateinit var newFeedbackList: ArrayList<Feedback>
    private lateinit var recyclerView: RecyclerView
    private var listener : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        var searchItem: MenuItem = menu.findItem(R.id.action_search) as MenuItem
        searchItem.isVisible = true

        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(strQuery: String?): Boolean {
                // Filter recyclerview based on query
                newFeedbackList.clear()
                if(strQuery!!.isNotEmpty()){
                    feedbackList.forEach{
                        if(it.type.lowercase() == strQuery?.lowercase()){
                            newFeedbackList.add(it)
                        }
                    }

                    recyclerView.adapter?.notifyDataSetChanged()
                }else {
                    newFeedbackList.clear()
                    newFeedbackList.addAll(feedbackList)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val searchText = newText!!.lowercase()
                newFeedbackList.clear()
                if(searchText.isNotEmpty()){
                    feedbackList.forEach{
                        if(it.type.startsWith(searchText, true)){
                            newFeedbackList.add(it)
                        }
                    }

                    recyclerView.adapter?.notifyDataSetChanged()
                }else{
                    newFeedbackList.clear()
                    newFeedbackList.addAll(feedbackList)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                return false
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackListBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Feedback List"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Recycler view
        recyclerView = binding.feedbackListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        feedbackList = arrayListOf()
        newFeedbackList = arrayListOf()

        recyclerView.adapter = FeedbackAdapter(newFeedbackList) { feedback ->
            // Prompt dialog with the selected feedback's details
            openFeedbackDialog(feedback)
        }

        // Listen to changes in Firestore and update recyclerview in real time
        listener = db.collection("feedback")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                getFeedbacksFromFirebase()
            }
    }

    private fun getFeedbacksFromFirebase() {
        // Reset all current lists
        newFeedbackList.clear()
        feedbackList.clear()

        db.collection("feedback").get().addOnSuccessListener { documents ->
            for (document in documents) {
                var feedbackType = document.get("type").toString()
                var feedbackId = document.id
                var facility = document.get("facility").toString()
                var comment = document.get("comment").toString()
                var newName: String = document.get("name").toString()
                var newStartingHour: String = document.get("starting_hour").toString()
                var newClosingHour: String = document.get("closing_hour").toString()
                var newStreet: String = document.get("address_street").toString()
                var newPostcode: String = document.get("address_postcode").toString()
                var newCity: String = document.get("address_city").toString()
                var newState: String = document.get("address_state").toString()
                var newOKUFeature: String = document.get("oku_feature").toString()
                var suggestion: String = ""

                if (feedbackType == "Incorrect Name") {
                    suggestion = newName
                } else if (feedbackType == "Incorrect Operation Hours") {
                    suggestion = "$newStartingHour - $newClosingHour"
                } else if (feedbackType == "Incorrect Address") {
                    suggestion = "$newStreet, $newPostcode $newCity, $newState"
                } else if (feedbackType == "Incorrect OKU Feature") {
                    suggestion = newOKUFeature
                }

                var feedback = Feedback(
                    comment,
                    feedbackId,
                    facility,
                    feedbackType,
                    suggestion,
                    newName,
                    newStartingHour,
                    newClosingHour,
                    newStreet,
                    newPostcode,
                    newCity,
                    newState,
                    newOKUFeature
                )
                feedbackList.add(feedback)
                newFeedbackList.add(feedback)
                recyclerView.adapter?.notifyDataSetChanged()
            }

            if(newFeedbackList.size == 0){
                recyclerView.adapter = null
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

        var facilityName: String = ""

        db.collection("facility").document(feedback.facility).get()
            .addOnSuccessListener { eachFacility ->
                if (eachFacility.id == feedback.facility)
                    facilityName = eachFacility.get("name") as String
            }

        // Get the associated facility
        db.collection("facility").document(feedback.facility).get().addOnSuccessListener {
            textViewFacilityName.text = facilityName

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
                "Incorrect OKU Feature" -> textViewCurrentInfo.text =
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
                    // update name
                    facilityRef.update("name", feedback.newName).addOnSuccessListener {
                        Toast.makeText(context, "Facility Name Updated", Toast.LENGTH_SHORT).show()
                        deleteFeedback(feedback.id)
                    }
                }
                "Incorrect Operation Hours" -> {
                    // update starting hour
                    facilityRef.update("starting_hour", feedback.newStartingHour)
                        .addOnSuccessListener {

                            // update closing hour
                            facilityRef.update("closing_hour", feedback.newClosingHour)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Facility Operating Hours Updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    deleteFeedback(feedback.id)
                                }
                        }
                }
                "Incorrect Address" -> {
                    // update city
                    facilityRef.update("address_city", feedback.newCity).addOnSuccessListener {

                        // update postcode
                        facilityRef.update("address_postcode", feedback.newPostcode)
                            .addOnSuccessListener {

                                // update state
                                facilityRef.update("address_state", feedback.newState)
                                    .addOnSuccessListener {

                                        // update street
                                        facilityRef.update("address_street", feedback.newStreet)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Facility Address Updated",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                deleteFeedback(feedback.id)
                                            }
                                    }
                            }
                    }
                }
                "Incorrect OKU Feature" -> {
                    // update oku feature
                    facilityRef.update("oku_feature", feedback.newOKUFeature)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Facility OKU Feature Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            deleteFeedback(feedback.id)
                        }
                }
            }

            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            deleteFeedback(feedback.id)
            dialog.dismiss()
        }
    }

    private fun deleteFeedback(feedbackID: String) {
        // Delete the feedback
        db.collection("feedback").document(feedbackID).delete().addOnSuccessListener {
            Toast.makeText(context, "Feedback Deleted", Toast.LENGTH_SHORT).show()
        }
    }

}