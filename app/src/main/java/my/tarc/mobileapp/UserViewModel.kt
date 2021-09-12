package my.tarc.mobileapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.net.URL

class UserViewModel : ViewModel() {
    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            loadUsers()
        }
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }

    private fun loadUsers() {
        lateinit var id: String
        lateinit var type: String
        lateinit var username: String
        lateinit var password: String
        lateinit var email: String
        lateinit var fullName: String
        lateinit var profilePicture: URL
        lateinit var favouriteFacility: Array<String>
    }
}