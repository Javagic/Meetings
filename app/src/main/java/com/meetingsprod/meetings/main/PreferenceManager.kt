package com.meetingsprod.meetings.main

import android.content.Context

const val AUTH_PREFERENCE = "AUTH_PREFERENCE"
const val KEY = "KEY"

object PreferenceManager {

    fun savePassword(password: String) {
        App.instance.getSharedPreferences(AUTH_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, password)
            .apply()
    }

}