package my.tarc.mobileapp.model

class Feedback(
    val comment: String,
    val id: String,
    val facility: String,
    val type: String,
    val suggestion: String
) {
    constructor(id: String, facility: String, type: String) : this("", "", facility, type, "") {}
}
