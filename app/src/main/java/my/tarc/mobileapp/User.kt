package my.tarc.mobileapp

import java.net.URL

data class User(
    val id: String,
    val type: String,
    val username: String,
    val password: String,
    val email: String,
    val fullName: String,
    val profilePicture: URL,
    val favouriteFacility: Array<String>
) {
    override fun toString(): String {
        return "$id : $username"
    }
}

