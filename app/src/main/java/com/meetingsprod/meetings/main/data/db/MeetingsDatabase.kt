/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 13.11.18 21:10
 */

package com.meetingsprod.meetings.main.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.meetingsprod.meetings.main.data.pojo.Meeting


@Database(entities = [Meeting::class], version = 1)
abstract class MeetingsDatabase : RoomDatabase() {
    abstract fun meetingsDao(): MeetingsDao
}