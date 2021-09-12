package my.tarc.mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class FacilityAdapter internal constructor() : RecyclerView.Adapter<FacilityAdapter.ViewHolder>() {
    private var facilityList = ArrayList<Facility>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val facilityImage: ShapeableImageView = itemView.findViewById(R.id.facilityLayout_imageView)
        val facilityName: TextView = itemView.findViewById(R.id.facilityLayout_txtFacilityName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.facility_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = facilityList[position]
        holder.facilityImage.setImageResource(currentItem.facilityImage)
        holder.facilityName.text = currentItem.facilityName
        holder.itemView.setOnClickListener {
            Toast.makeText(
                it.context,
                "Facility Name:" + facilityList[position].facilityName,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return facilityList.size
    }

    // Pass the list of facility into this adapter
    internal fun setFacility(facility: ArrayList<Facility>) {
        facilityList = facility
    }
}