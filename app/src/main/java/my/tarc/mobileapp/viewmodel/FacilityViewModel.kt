package my.tarc.mobileapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.tarc.mobileapp.model.Facility

class FacilityViewModel : ViewModel() {
    private val mutableSelectedFacility = MutableLiveData<Facility>()
    val selectedFacility: LiveData<Facility> get() = mutableSelectedFacility

    fun setFacility(facility: Facility) {
        mutableSelectedFacility.value = facility
    }
}