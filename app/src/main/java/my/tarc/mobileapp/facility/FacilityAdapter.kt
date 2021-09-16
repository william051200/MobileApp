package my.tarc.mobileapp.facility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.tarc.mobileapp.R
import my.tarc.mobileapp.model.Facility

class FacilityAdapter(
    private val facilityList: ArrayList<Facility>,
    private val listener: (Facility) -> Unit
) :
    RecyclerView.Adapter<FacilityAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val facilityImage: ImageView = itemView.findViewById(R.id.facilityLayout_imageView)
        val facilityName: TextView = itemView.findViewById(R.id.facilityLayout_txtFacilityName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.facility_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = facilityList[position]
        holder.facilityImage.setImageBitmap(currentItem.picture)
        holder.facilityName.text = currentItem.name
        holder.itemView.setOnClickListener{listener(currentItem)}
    }

    override fun getItemCount(): Int {
        return facilityList.size
    }
}