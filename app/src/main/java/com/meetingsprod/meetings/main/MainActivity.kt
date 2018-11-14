/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 13.11.18 18:36
 */

package com.meetingsprod.meetings.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.App.Companion.meetingsDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


class MainActivity : AppCompatActivity() {
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvMeetings) }
    private val adapter = MeetingsAdapter()
    private val disposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        MeetingsRepository.getMeetings().subscribe({ }, { })
    }

    public override fun onStart() {
        super.onStart()
        disposable.add(
            meetingsDao.allFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe {
                adapter.data = it.toMutableList()
            }
        )
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}
