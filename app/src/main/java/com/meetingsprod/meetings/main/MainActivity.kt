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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.App.Companion.database
import com.meetingsprod.meetings.main.App.Companion.meetingsDao
import com.meetingsprod.meetings.main.data.pojo.Meeting
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvMeetings) }
    private val adapter = MeetingsAdapter()
    private val disposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        mAuth.currentUser?.uid?.let {
            firestore.collection("Meetings").get().addOnSuccessListener {
                if (!it.isEmpty) {
                    it.documents.toMutableList().map {
                        it.toObject(Meeting::class.java)!!
                    }
                        .also {
                            Observable.just(it)
                                .observeOn(Schedulers.io())
                                .subscribe {
                                    database.runInTransaction {
                                        it.forEach {
                                            meetingsDao.insert(it)
                                        }
                                    }
                                }
                        }
                }
            }
        }
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
