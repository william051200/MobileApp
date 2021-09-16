package my.tarc.mobileapp.facility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.tarc.mobileapp.R
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.Feedback

class FeedbackAdapter(
    private val feedbackList: ArrayList<Feedback>,
    private val listener: (Feedback) -> Unit
) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedbackTitle: TextView = itemView.findViewById(R.id.feedbackLayout_txtFeedbackTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.feedback_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = feedbackList[position]
        holder.feedbackTitle.text = currentItem.type
        holder.itemView.setOnClickListener{listener(currentItem)}
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }
}