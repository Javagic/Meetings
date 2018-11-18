
package com.meetingsprod.meetings.main.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.api.MeetingsRepository
import com.meetingsprod.meetings.main.api.Priority
import com.meetingsprod.meetings.main.data.pojo.Meeting
import com.meetingsprod.meetings.main.data.pojo.Member
import io.reactivex.disposables.CompositeDisposable

class CreateMeetingActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meeting)
    }

    override fun onStart() {
        super.onStart()
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val tvStartDate = findViewById<TextView>(R.id.tvStartDate)
        val tvEndDate = findViewById<TextView>(R.id.tvEndDate)
        val members = findViewById<TextView>(R.id.tvMembers)
        val priority = findViewById<TextView>(R.id.tvPriority)
        val createBtn = findViewById<TextView>(R.id.btnCreateNew)
        createBtn.setOnClickListener {
            disposable.add(
                MeetingsRepository.addDocument(
                    Meeting(
                        name = tvName.text.toString(),
                        description = tvDescription.text.toString(),
                        startDate = tvStartDate.text.toString(),
                        endDate = tvEndDate.text.toString(),
                        members = mutableListOf(Member(members.text.toString(),position = "ads")),
                        priority = Priority.fromString(priority.text.toString())
                    )
                ).subscribe({ finish() }, {
                    Toast.makeText(
                        applicationContext?.applicationContext,
                        applicationContext?.resources?.getString(R.string.create_error), Toast.LENGTH_LONG
                    )
                        .show()
                })
            )
        }

    }

    override fun onStop() {
        super.onStop()
    }
}