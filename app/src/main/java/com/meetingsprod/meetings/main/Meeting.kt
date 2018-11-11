
package com.meetingsprod.meetings.main

data class Meeting(
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val members: List<Member>,
    val priority: Priority
)

enum class Priority {
    EMERGENCY,
    PLANNED,
    MAYBE
}