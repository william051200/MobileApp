package my.tarc.mobileapp.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
                var feedbackTitle = document.get("title").toString()
                var feedbackId = document.get("id").toString()
                var feedback = Feedback(feedbackId, feedbackTitle)
                feedbackList.add(feedback)
            }
            recyclerView.adapter = FeedbackAdapter(feedbackList)
        }
    }
}