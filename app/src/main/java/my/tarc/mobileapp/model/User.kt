package my.tarc.mobileapp.model

class User(
    val email: String,
    val name: String,
    val userType: String,
    val favoriteFacilities: ArrayList<Facility>
) {
}