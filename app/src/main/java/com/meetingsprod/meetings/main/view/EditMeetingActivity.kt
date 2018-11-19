package com.meetingsprod.meetings.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.App.Companion.database
import com.meetingsprod.meetings.main.api.MeetingsRepository
import com.meetingsprod.meetings.main.api.Priority
import com.meetingsprod.meetings.main.data.pojo.Meeting
import com.meetingsprod.meetings.main.data.pojo.Member
import com.meetingsprod.meetings.main.utils.PreferenceManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

const val EXTRA_ID = "MeetingId"

class EditMeetingActivity : AppCompatActivity() {
    private val tvName by lazy { findViewById<EditText>(R.id.tvName) }
    private val tvDescription by lazy { findViewById<EditText>(R.id.tvDescription) }
    private val tvStartDate by lazy { findViewById<EditText>(R.id.tvStartDate) }
    private val tvEndDate by lazy { findViewById<EditText>(R.id.tvEndDate) }
    private val members by lazy { findViewById<EditText>(R.id.tvMembers) }
    private val rbMaybe by lazy { findViewById<RadioButton>(R.id.maybe) }
    private val rbPlanned by lazy { findViewById<RadioButton>(R.id.planned) }
    private val rbEmergency by lazy { findViewById<RadioButton>(R.id.emergency) }
    private val groupPriority by lazy { findViewById<RadioGroup>(R.id.groupPriority) }
    private val createBtn by lazy { findViewById<Button>(R.id.btnCreateNew) }
    private val participateBtn by lazy { findViewById<Button>(R.id.btnParticipate) }

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meeting)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.delete -> {
            disposable.add(
                MeetingsRepository.deleteDocument(intent.getStringExtra(EXTRA_ID))
                    .subscribe({
                        finish()
                        showToast(R.string.toast_delete)
                    }, {
                        showToast(R.string.error_data)
                    })
            )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.getStringExtra(EXTRA_ID) != "-1")
            menuInflater.inflate(R.menu.info, menu)
        return true
    }


    override fun onStart() {
        super.onStart()
        if (intent.getStringExtra(EXTRA_ID) != "-1")
            loadInfo()
        createBtn.setOnClickListener {
            disposable.add(
                MeetingsRepository.addDocument(
                    Meeting(
                        name = tvName.text.toString(),
                        description = tvDescription.text.toString(),
                        startDate = tvStartDate.text.toString(),
                        endDate = tvEndDate.text.toString(),
                        members = if (intent.getStringExtra(EXTRA_ID) != "-1") mMembers.toMutableList()
                        else mutableListOf(
                            PreferenceManager.getUser()
                        ),
                        priority = Priority.fromString(findViewById<RadioButton>(groupPriority.checkedRadioButtonId).text.toString().toUpperCase())
                    )
                ).subscribe({ finish() }, {
                    showToast(R.string.create_error)
                })
            )
        }
    }

    private fun loadInfo() =
        disposable.add(
            Single.fromCallable { database.meetingsDao().get(intent.getStringExtra(EXTRA_ID)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onInfoLoaded) {
                    showToast(R.string.error_data)
                }
        )

    private fun onInfoLoaded(info: Meeting) {
        bind(info)
        if (info.members.none { it.name == PreferenceManager.getUserName() })
            participateBtn.visibility = View.VISIBLE
        participateBtn.setOnClickListener {
            MeetingsRepository.addMember(
                PreferenceManager.getUserName(),
                PreferenceManager.getUserName(), info
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.visibility = View.GONE

                }
        }

    }

    companion object {
        fun start(context: Context, meetingId: String = "-1") {
            context.startActivity(Intent(context, EditMeetingActivity::class.java).apply {
                putExtra(EXTRA_ID, meetingId)
            })
        }
    }

    fun bind(meeting: Meeting) {
        tvName.setText(meeting.name)
        tvDescription.setText(meeting.description)
        tvStartDate.setText(meeting.startDate)
        tvEndDate.setText(meeting.endDate)
        members.setText(meeting.members.joinToString { it.name })
        mMembers = meeting.members
        when (meeting.priority) {
            Priority.MAYBE -> rbMaybe.isChecked = true
            Priority.PLANNED -> rbPlanned.isChecked = true
            Priority.EMERGENCY -> rbEmergency.isChecked = true
        }
    }

    private fun showToast(message: Int) {
        Toast.makeText(
            this,
            resources?.getString(message), Toast.LENGTH_LONG
        )
            .show()

    }

    var mMembers: List<Member> = ArrayList()
}