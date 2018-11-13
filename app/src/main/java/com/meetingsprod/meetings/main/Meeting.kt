package com.meetingsprod.meetings.main

data class Meeting(
    var name: String = "",
    var description: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var members: List<Member> = ArrayList(),
    var priority: String = ""

)

enum class Priority {
    EMERGENCY,
    PLANNED,
    MAYBE
}