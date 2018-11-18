package com.meetingsprod.meetings.main.utils

import android.content.Context
import com.meetingsprod.meetings.main.App

const val AUTH_PREFERENCE = "AUTH_PREFERENCE"
const val KEY_NAME = "KEY_NAME"
const val KEY_POSITION = "KEY_POSITION"

object PreferenceManager {

    fun saveUserName(name: String) {
        App.instance.getSharedPreferences(AUTH_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAME, name)
            .apply()
    }

    fun saveUserPosition(position: String) {
        App.instance.getSharedPreferences(AUTH_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_POSITION, position)
            .apply()
    }
    fun getUserName() =
        App.instance.getSharedPreferences(AUTH_PREFERENCE, Context.MODE_PRIVATE)
            .getString(KEY_NAME,"")

    fun getUserPosition() =
        App.instance.getSharedPreferences(AUTH_PREFERENCE, Context.MODE_PRIVATE)
            .getString(KEY_POSITION,"")
}