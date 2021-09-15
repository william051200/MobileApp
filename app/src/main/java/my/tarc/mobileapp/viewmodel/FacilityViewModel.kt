package my.tarc.mobileapp.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.tarc.mobileapp.facility.FacilityAdapter
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.Feedback

class FacilityViewModel : ViewModel() {
    // Firestore database
    private val db = Firebase.firestore

    // Firebase storage
    private val storage = Firebase.storage("gs://mobile-app-f3440.appspot.com")
    private var storageRef = storage.reference.child("Facility Images")

    private val mutableSelectedFacility = MutableLiveData<Facility>()
    val selectedFacility: LiveData<Facility> get() = mutableSelectedFacility

    private val mutableSelectedFeedback = MutableLiveData<Feedback>()
    val selectedFeedback: LiveData<Feedback> get() = mutableSelectedFeedback

    private val mutableFacilityList = MutableLiveData<List<Facility>>()
    val facilityList: LiveData<List<Facility>> get() = mutableFacilityList

    fun setFacility(facility: Facility) {
        mutableSelectedFacility.value = facility
    }

    fun setFeedback(feedback: Feedback) {
        mutableSelectedFeedback.value = feedback
    }

    fun getAllFacility(){
        db.collection("facility")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var id = document.get("id").toString()
                    var name = document.get("name").toString()
                    var address = document.get("address").toString()
//                    var
                }
            }
    }
}