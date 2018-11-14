/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 11.11.18 18:48
 */

package com.meetingsprod.meetings.main.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.meetingsprod.meetings.R

class MeetingsReceiver : BroadcastReceiver() {
    @SuppressLint("LogNotTimber")
    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.action?.equals(Intent.ACTION_BOOT_COMPLETED)
            ?.takeIf { it }?.let {
                Toast.makeText(
                    context?.applicationContext,
                    context?.resources?.getString(R.string.reciever_toast), Toast.LENGTH_LONG
                )
                    .show()
                Log.d("MeetingsReceiver", context?.resources?.getString(R.string.reciever_toast))
                context?.run {
                    MeetingService.startService(this)
                }
            }

    }

}