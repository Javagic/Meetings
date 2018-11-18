package com.meetingsprod.meetings.main.data.pojo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.meetingsprod.meetings.main.api.Priority
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "Meetings", primaryKeys = ["name"])
@TypeConverters(MemberTypeConverter::class)
data class Meeting(
    var name: String = "",
    var description: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var members: MutableList<Member> = ArrayList(),
    var priority: Priority = Priority.MAYBE
) {

    fun toMap() =
        HashMap<String, Any>().apply {
            put("name", name)
            put("description", description)
            put("startDate", startDate)
            put("endDate", endDate)
            put("members", Gson().toJson(members))
            put("priority", priority.toString())
        }


    fun clone() = Meeting(name, description, startDate, endDate, mutableListOf<Member>().apply {
        members.forEach { add(it.clone()) }
    }, priority)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Meeting
        return name == other.name
    }

    override fun hashCode(): Int = name.hashCode()
}


class MemberTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toMemberList(string: String?): List<Member> = if (string.isNullOrBlank())
        emptyList() else gson.fromJson(string, object : TypeToken<List<Member>>() {}.type)

    @TypeConverter
    fun toJson(points: List<Member>): String = gson.toJson(points)

    @TypeConverter
    fun toPriority(value: Int?): Priority = Priority.values()[value ?: 0]

    @TypeConverter
    fun fromPriority(priority: Priority): Int = priority.ordinal
}