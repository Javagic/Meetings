/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 11.11.18 18:52
 */

package com.meetingsprod.meetings.main

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.widget.Toast
import com.meetingsprod.meetings.R

class MeetingService : JobService() {
    companion object {

        private const val MEETINGS_JOB_ID = 1505
        private const val REPEAT_MILLIS = 10_000L

        private fun createJobInfo(context: Context): JobInfo =
            JobInfo.Builder(MEETINGS_JOB_ID, ComponentName(context, MeetingService::class.java))
                .setPeriodic(REPEAT_MILLIS)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build()

        fun startService(context: Context) {
            (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).apply {
                if (!allPendingJobs.any { it.id == MEETINGS_JOB_ID })
                    schedule(createJobInfo(context))
            }
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        Toast.makeText(
            applicationContext?.applicationContext,
            applicationContext?.resources?.getString(R.string.job_toast), Toast.LENGTH_LONG
        )
            .show()
        return true
    }


}
