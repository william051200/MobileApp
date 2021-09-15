package my.tarc.mobileapp.model

import android.graphics.Bitmap

class Facility(
    val id: String,
    val name: String,
    val address: Address,
    val category: String,
    val feedbacks: Array<Feedback>,
    val okuFeature: String,
    val startingHour: String,
    val closingHour: String,
    val rating: Int,
    val status: String
) {
    var picture: Bitmap? = null

    constructor(picture: Bitmap?, name: String, state: String) : this(
        "",
        name,
        Address("",0,"",state),
        "",
        arrayOf(),
        "",
        "",
        "",
        -1,
        ""
    ) {
        this.picture = picture
    }
}