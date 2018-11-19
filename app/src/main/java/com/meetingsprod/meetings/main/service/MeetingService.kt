package com.meetingsprod.meetings.main.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.App.Companion.database
import com.meetingsprod.meetings.main.api.MeetingsRepository
import com.meetingsprod.meetings.main.view.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MeetingService : JobService() {
    companion object {
        private const val MEETINGS_JOB_ID = 1506
        private const val REPEAT_MILLIS = 10_000L
        private const val NEW_MEETING_CHANNEL_ID = "NewMeetingNotificationChannel"
        private const val NEW_MEETING_NOTIFICATION_ID = 313

        private fun createJobInfo(context: Context, isFirstLaunch: Boolean): JobInfo =
            JobInfo.Builder(MEETINGS_JOB_ID, ComponentName(context, MeetingService::class.java))
                .apply {
                    if (isFirstLaunch) {
                        setMinimumLatency(0L)
                        setOverrideDeadline(10000L)
                    } else {
                        val residual = 1_000
                        setMinimumLatency(REPEAT_MILLIS - residual)
                        setOverrideDeadline(REPEAT_MILLIS + residual)
                    }

                }
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build()

        fun startService(context: Context) {
            (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).apply {
                if (allPendingJobs.none { it.id == MEETINGS_JOB_ID })
                    schedule(createJobInfo(context, true))
            }
        }

    }


    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    @SuppressLint("CheckResult")
    override fun onStartJob(params: JobParameters?): Boolean {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(MAIL, KEY)
            .addOnCompleteListener {
                MeetingsRepository.getMeetings()
                    .observeOn(Schedulers.io())
                    .subscribe({
                        val diff = it - database.meetingsDao().all()
                        if (diff.isNotEmpty())
                            displayNotification(diff.size)
                        finishJob(params)
                    }, {
                        Observable.fromCallable {
                            Toast.makeText(
                                applicationContext?.applicationContext,
                                it.message, Toast.LENGTH_LONG
                            )
                                .show()
                        }
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe({ }, {})
                        finishJob(params)
                    })
            }
        Toast.makeText(
            applicationContext?.applicationContext,
            applicationContext?.resources?.getString(R.string.job_toast), Toast.LENGTH_LONG
        )
            .show()

        return true
    }

    private fun finishJob(params: JobParameters?) {
        restart()
        jobFinished(params, false)
    }

    private fun restart() {
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(
            createJobInfo(applicationContext, false)
        )
    }

    private fun displayNotification(new: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, NEW_MEETING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification_new_meting, new)
            )
            .setContentText(applicationContext.getString(R.string.notification_content))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext, 0,
                    Intent(applicationContext, MainActivity::class.java), 0
                )
            )
            .build()
        notificationManager.notify(NEW_MEETING_NOTIFICATION_ID, notification)
    }
}

const val MAIL = "zufar.sunagatov@gmail.com"
const val KEY = "123456"
