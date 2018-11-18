
package com.meetingsprod.meetings.main.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.meetingsprod.meetings.main.data.pojo.Meeting


@Database(entities = [Meeting::class], version = 1)
abstract class MeetingsDatabase : RoomDatabase() {
    abstract fun meetingsDao(): MeetingsDao
}