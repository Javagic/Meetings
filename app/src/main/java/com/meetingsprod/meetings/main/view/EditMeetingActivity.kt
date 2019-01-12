package com.meetingsprod.meetings.main.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
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
import com.meetingsprod.meetings.main.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val EXTRA_ID = "MeetingId"
val RECORD_PERMISSIONS = Manifest.permission.RECORD_AUDIO
val EXTERNAL_STORAGE_PERMISSIONS = Manifest.permission.WRITE_EXTERNAL_STORAGE

class EditMeetingActivity : AppCompatActivity(), DateTimePickerListener {
    companion object {
        fun start(context: Context, meetingId: String = "-1") {
            context.startActivity(Intent(context, EditMeetingActivity::class.java).apply {
                putExtra(EXTRA_ID, meetingId)
            })
        }
    }

    private val tvName by lazy { findViewById<EditText>(R.id.tvName) }
    private val tvDescription by lazy { findViewById<EditText>(R.id.tvDescription) }
    private val tvStartDate by lazy { findViewById<TextInputLayout>(R.id.tvStartDate) }
    private val tvEndDate by lazy { findViewById<TextInputLayout>(R.id.tvEndDate) }
    private val members by lazy { findViewById<EditText>(R.id.tvMembers) }
    private val rbMaybe by lazy { findViewById<RadioButton>(R.id.maybe) }
    private val rbPlanned by lazy { findViewById<RadioButton>(R.id.planned) }
    private val rbEmergency by lazy { findViewById<RadioButton>(R.id.emergency) }
    private val groupPriority by lazy { findViewById<RadioGroup>(R.id.groupPriority) }
    private val createBtn by lazy { findViewById<Button>(R.id.btnCreateNew) }
    private val participateBtn by lazy { findViewById<Button>(R.id.btnParticipate) }
    private val audioBtn by lazy { findViewById<Button>(R.id.btnAudio) }

    private val disposable = CompositeDisposable()

    lateinit var recorder: MediaRecorder
    lateinit var player: MediaPlayer
    var file: File? = null

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


    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        if (intent.getStringExtra(EXTRA_ID) != "-1") {
            loadInfo()
        } else {
            rbMaybe.isChecked = true
        }
        tvStartDate.editText!!.setText(dateTimeFormat.format(Date()))
        tvEndDate.editText!!.setText(dateTimeFormat.format(Date()))
        tvStartDate.editText!!.setOnClickListener {
            showDatePickerDialog(this, tvStartDate.editText!!)
        }
        tvEndDate.editText!!.setOnClickListener {
            showDatePickerDialog(this, tvEndDate.editText!!)
        }
        createBtn.setOnClickListener {
            disposable.add(
                MeetingsRepository.addDocument(
                    Meeting(
                        name = tvName.text.toString(),
                        description = tvDescription.text.toString(),
                        startDate = tvStartDate.editText!!.text.toString(),
                        endDate = tvEndDate.editText!!.text.toString(),
                        audioPath = file?.absolutePath?:"",
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
        if (intent.getStringExtra(EXTRA_ID) == "-1") {
            audioBtn.setOnClickListener {
                startRecord()
            }
        } else {
            Single.fromCallable { database.meetingsDao().get(intent.getStringExtra(EXTRA_ID)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.audioPath.isNotEmpty()) {
                        file = File(it.audioPath)
                            setupPlayer()
                    } else {
                        audioBtn.visibility = View.GONE
                    }
                }, {
                    showToast(R.string.error_data)
                })
        }


    }


    fun startRecord() {
        withAudioPermission(1) {
            audioBtn.isEnabled = false
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
            val path = File(Environment.getExternalStorageDirectory().path)
            try {
                file = File.createTempFile(timeStamp, ".3gp", path)
            } catch (e: IOException) {
            }
            recorder.setOutputFile(file?.absolutePath)
            try {
                recorder.prepare()
            } catch (e: IOException) {
            }
            recorder.start()
            audioBtn.text = getString(R.string.recording)
            audioBtn.setOnClickListener {
                recorder.stop()
                recorder.release()
                setupPlayer()
            }
            audioBtn.isEnabled = true
        }
    }

    private fun setupPlayer() {
        player = MediaPlayer()
        try {
            player.setDataSource(file?.absolutePath)
        } catch (e: IOException) {
        }

        try {
            player.prepare()
        } catch (e: IOException) {
        }
        audioBtn.text = getString(R.string.play)
        audioBtn.setOnClickListener {
            player.start()
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

    fun bind(meeting: Meeting) {
        tvName.setText(meeting.name)
        tvDescription.setText(meeting.description)
        tvStartDate.editText!!.setText(meeting.startDate)
        tvEndDate.editText!!.setText(meeting.endDate)
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

    override fun onDateSet(date: Date, editText: EditText) {
        editText.setText(dateFormat.format(date))
        showTimePickerDialog(this, editText)
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(date: Date, editText: EditText) {
        editText.setText("${editText.text} ${timeFormat.format(date)}")
    }

    var mMembers: List<Member> = ArrayList()

    private fun withAudioPermission(requestCode: Int, action: () -> Unit) = withPermissions(
        listOf(EXTERNAL_STORAGE_PERMISSIONS, RECORD_PERMISSIONS),
        R.string.need_permission,
        requestCode,
        action
    )

    private fun withPermissions(
        permissions: List<String>, @StringRes rationale: Int,
        requestCode: Int,
        action: () -> Unit
    ) =
        if (hasPermissions(permissions)) {
            action()
        } else {
            EasyPermissions.requestPermissions(
                this,
                this.getString(rationale),
                requestCode,
                *permissions.toTypedArray()
            )
        }

    private fun Context.hasPermissions(permissions: List<String>): Boolean = EasyPermissions.hasPermissions(
        this,
        *permissions.toTypedArray()
    )
}