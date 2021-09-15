package my.tarc.mobileapp.model

class Feedback(val comment: String, val id: String, val title: String, val suggestion: String) {
    constructor(id: String, title: String) : this("", "", title, "") {}
}
