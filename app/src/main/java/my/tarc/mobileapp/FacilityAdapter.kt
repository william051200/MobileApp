package my.tarc.mobileapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class FacilityAdapter(private val facilityList: ArrayList<Facility>) :
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
        holder.itemView.setOnClickListener {
            Toast.makeText(
                it.context,
                "Facility Name:" + facilityList[position].name,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return facilityList.size
    }
}