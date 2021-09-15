package my.tarc.mobileapp.facility

import android.graphics.Bitmap

class Facility(
    val id: String,
    val name: String,
    val address_street: String,
    val address_postcode: String,
    val address_city: String,
    val address_state: String,
    val category: String,
    val feedbacks: Array<String>,
    val okuFeature: String,
    val startingHour: String,
    val closingHour: String,
    val rating: Int,
    val status: String
) {
    var picture: Bitmap? = null

    constructor(picture: Bitmap?, name: String, address_state: String) : this(
        "",
        name,
        "",
        "",
        "",
        address_state,
        "",
        arrayOf(""),
        "",
        "",
        "",
        -1,
        ""
    ) {
        this.picture = picture
    }
}
