package my.tarc.mobileapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.Feedback

class FacilityViewModel : ViewModel() {
    private val mutableSelectedFacility = MutableLiveData<Facility>()
    val selectedFacility: LiveData<Facility> get() = mutableSelectedFacility

    private val mutableSelectedFeedback = MutableLiveData<Feedback>()
    val selectedFeedback: LiveData<Feedback> get() = mutableSelectedFeedback

    fun setFacility(facility: Facility) {
        mutableSelectedFacility.value = facility
    }

    fun setFeedback(feedback: Feedback) {
        mutableSelectedFeedback.value = feedback
    }
}