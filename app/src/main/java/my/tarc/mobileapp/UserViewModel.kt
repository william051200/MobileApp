package my.tarc.mobileapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class UserViewModel(application: Application) : AndroidViewModel(application) {
    var id: String = ""
    var type: String = ""
    var username: String = ""
    var password: String = ""
    var email: String = ""
    var fullName: String = ""
    var profilePicture = ""
    var favouriteFacility = emptyArray<String>()

//    init {
//        Log.d("ViewModel", "Initialize")
//        val contactDao = ContactDatabase.getDatabase(application).contactDao()
//        repository = ContactRepository(contactDao)
//        contactList = repository.allContact
//    }
}