package com.meetingsprod.meetings.main

import android.app.Application
import android.arch.persistence.room.Room
import com.meetingsprod.meetings.main.data.db.MeetingsDao
import com.meetingsprod.meetings.main.data.db.MeetingsDatabase
import com.meetingsprod.meetings.main.service.MeetingService

val APP_DATABASE = "appDatabase.db"

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
        lateinit var meetingsDao: MeetingsDao
            private set
        lateinit var database:MeetingsDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
            this,
            MeetingsDatabase::class.java,
            APP_DATABASE
        ).fallbackToDestructiveMigration()
            .build()
        meetingsDao = database.meetingsDao()
        MeetingService.startService(applicationContext)
    }
}