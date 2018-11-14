package com.meetingsprod.meetings.main.data.pojo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "Meetings", primaryKeys = ["name"])
@TypeConverters(MemberTypeConverter::class)
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

class MemberTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toMemberList(string: String?): List<Member> = if (string.isNullOrBlank())
        emptyList() else gson.fromJson(string, object : TypeToken<List<Member>>() {}.type)

    @TypeConverter
    fun toJson(points: List<Member>): String = gson.toJson(points)
}