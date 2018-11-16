/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 13.11.18 18:36
 */

package com.meetingsprod.meetings.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.App.Companion.meetingsDao
import com.meetingsprod.meetings.main.api.MeetingsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


class MainActivity : AppCompatActivity() {
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvMeetings) }
    private val fabCreate by lazy { findViewById<FloatingActionButton>(R.id.fabCreate) }
    private val adapter = MeetingsAdapter { MeetingInfoActivity.start(this, it) }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ItemOffsetDecoration())
        fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateMeetingActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.refresh -> {
            requestMeetings()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    public override fun onStart() {
        super.onStart()
        disposable.add(
            meetingsDao.allFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe {
                adapter.data = it.toMutableList()
            }
        )
        requestMeetings()
    }


    private fun requestMeetings() {
        disposable.add(
            MeetingsRepository.getMeetings().subscribe({ }, {
                Toast.makeText(
                    this,
                    resources?.getString(R.string.error_connection), Toast.LENGTH_LONG
                )
                    .show()
            })
        )
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}
