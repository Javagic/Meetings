
package com.meetingsprod.meetings.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.utils.PreferenceManager
import com.meetingsprod.meetings.main.api.MeetingsRepository
import com.meetingsprod.meetings.main.data.pojo.Meeting
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

const val EXTRA_ID = "MeetingId"

class MeetingInfoActivity : AppCompatActivity() {
    private val disposable: CompositeDisposable = CompositeDisposable()
    private val participateBtn by lazy { findViewById<Button>(R.id.btnParticipate) }

    companion object {
        fun start(context: Context, meetingId: String) {
            context.startActivity(Intent(context, MeetingInfoActivity::class.java).apply {
                putExtra(EXTRA_ID, meetingId)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.refresh -> {
            disposable.add(
                MeetingsRepository.deleteDocument(intent.getStringExtra(EXTRA_ID))//TODO handle it
                    .subscribe({
                        finish()
                        Toast.makeText(
                            this,
                            resources?.getString(R.string.toast_delete), Toast.LENGTH_LONG
                        )
                            .show()
                    }, {
                        Toast.makeText(
                            this,
                            resources?.getString(R.string.error_data), Toast.LENGTH_LONG
                        )
                            .show()
                    })
            )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        val meetingId = intent.getStringExtra(EXTRA_ID)
        disposable.add(
            MeetingsRepository.getMeetingInfo(meetingId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ info ->
                    bind(info)
                    if (info.members.none { it.name == PreferenceManager.getUserName() }) participateBtn.visibility = View.VISIBLE
                    participateBtn.setOnClickListener {
                        MeetingsRepository.addMember(
                            PreferenceManager.getUserName(),
                            PreferenceManager.getUserName(), info)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                it.visibility = View.GONE

                            }
                    }
                }, {
                    Toast.makeText(
                        this,
                        resources?.getString(R.string.error_data), Toast.LENGTH_LONG
                    )
                        .show()
                })
        )
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    fun bind(meeting: Meeting) {
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val tvStartDate = findViewById<TextView>(R.id.tvStartDate)
        val tvEndDate = findViewById<TextView>(R.id.tvEndDate)
        val members = findViewById<TextView>(R.id.tvMembers)
        val priority = findViewById<TextView>(R.id.tvPriority)
        tvName.text = meeting.name
        tvDescription.text = meeting.description
        tvStartDate.text = meeting.startDate
        tvEndDate.text = meeting.endDate
        members.text = meeting.members.toString()
        priority.text = meeting.priority.name

    }
}