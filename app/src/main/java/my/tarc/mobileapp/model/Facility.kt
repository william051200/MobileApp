package my.tarc.mobileapp.model

import android.graphics.Bitmap

class Facility(
    var id: String,
    var name: String,
    var address: Address,
    var category: String,
    var feedbacks: Array<Feedback>,
    var okuFeature: String,
    var startingHour: String,
    var closingHour: String,
    var rating: Int,
    var status: String
) {
    var picture: Bitmap? = null

    constructor(id: String) : this(
        id,
        "",
        Address("", 0, "", ""),
        "",
        arrayOf(),
        "",
        "",
        "",
        -1,
        ""
    )

    constructor(picture: Bitmap?, name: String, state: String, id: String) : this(
        id,
        name,
        Address("", 0, "", state),
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
