package com.meetingsprod.meetings.main.api

import com.google.gson.Gson
import com.meetingsprod.meetings.main.data.pojo.Meeting
import com.meetingsprod.meetings.main.data.pojo.Member

data class MeetingApiResponse(
    var name: String = "",
    var description: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var members: String? = "",
    var priority: String? = "",
    var audio: String? = ""
) {

    fun toDataClass() = Meeting(
        name,
        description,
        startDate,
        endDate,
        members?.let { Gson().fromJson(members, Array<Member>::class.java)?.toMutableList() } ?: mutableListOf(),
        priority?.let { Priority.fromString(it) } ?: Priority.PLANNED,
        audio ?: ""
    )
}

enum class Priority {
    EMERGENCY,
    PLANNED,
    MAYBE;

    companion object {
        fun fromString(str: String) = when (str) {
            "EMERGENCY" -> EMERGENCY
            "PLANNED" -> PLANNED
            else -> MAYBE
        }
    }
}