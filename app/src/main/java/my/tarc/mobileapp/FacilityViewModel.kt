package my.tarc.mobileapp

import android.util.Log
import androidx.lifecycle.ViewModel

class FacilityViewModel : ViewModel() {
    private val facilityList = ArrayList<Facility>()

    init {
        Log.d("ViewModel", "Initialize")
    }

    fun addFacility(facility: Facility) {
        facilityList.add(facility)
    }

    fun removeFacility(facility: Facility) {
        facilityList.remove(facility)
    }

    fun getFacilities(): ArrayList<Facility> {
        return facilityList
    }

    fun getSize(): Int {
        return facilityList.size
    }

    fun sortAscending() {
        facilityList.sortBy { it.name }
    }

    fun sortDescending() {
        facilityList.sortByDescending { it.name }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "Cleared")
    }
}