package my.tarc.mobileapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.tarc.mobileapp.model.Facility
import my.tarc.mobileapp.model.User

class UserViewModel : ViewModel() {
    private val mutableActiveUser = MutableLiveData<User>()
    val activeUser : LiveData<User> get() = mutableActiveUser

    fun setUser(user : User){
        mutableActiveUser.value = user
    }
}