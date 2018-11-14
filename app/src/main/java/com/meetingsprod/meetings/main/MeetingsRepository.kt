/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 14.11.18 21:26
 */

package com.meetingsprod.meetings.main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meetingsprod.meetings.main.data.pojo.Meeting
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object MeetingsRepository {
    fun getMeetings(): Single<List<Meeting>> = meetingsSingle().subscribeOn(Schedulers.io())


    private fun meetingsSingle(): Single<List<Meeting>> = Single.create { emitter ->
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("Meetings").get().addOnSuccessListener {
                if (!it.isEmpty) {
                    it.documents.toMutableList().map {
                        it.toObject(Meeting::class.java)!!
                    }
                        .also {
                            Observable.just(it)
                                .observeOn(Schedulers.io())
                                .subscribe {
                                    App.database.runInTransaction {
                                        it.forEach {
                                            App.meetingsDao.insert(it)
                                        }
                                    }
                                }
                        }
                        .also { emitter.onSuccess(it) }
                }
            }
                .addOnFailureListener { emitter.onError(it) }
        }
    }
}