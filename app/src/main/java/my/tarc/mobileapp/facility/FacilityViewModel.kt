package my.tarc.mobileapp.facility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FacilityViewModel : ViewModel() {
    private val mutableSelectedFacility = MutableLiveData<Facility>()
    val selectedFacility: LiveData<Facility> get() = mutableSelectedFacility

    fun setFacility(facility: Facility) {
        mutableSelectedFacility.value = facility
    }
}