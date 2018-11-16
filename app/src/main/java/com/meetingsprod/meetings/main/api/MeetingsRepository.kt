/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 14.11.18 21:26
 */

package com.meetingsprod.meetings.main.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meetingsprod.meetings.main.App
import com.meetingsprod.meetings.main.data.pojo.Meeting
import com.meetingsprod.meetings.main.data.pojo.Member
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object MeetingsRepository {
    fun getMeetings(): Single<List<Meeting>> = meetingsSingle().subscribeOn(Schedulers.io())

    fun getMeetingInfo(id: String): Single<Meeting> = Single.create { emitter ->
        withUserCredentials {
            FirebaseFirestore.getInstance().collection("Meetings").whereEqualTo("name", id).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        it.firstOrNull()
                            ?.toObject(MeetingApiResponse::class.java)
                            ?.let { it.toDataClass() }
                            ?.let { emitter.onSuccess(it) } /*?:emitter.onError()*/
                    }
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun deleteDocument(id: String): Completable = Completable.create { emitter ->
        withUserCredentials {
            FirebaseFirestore.getInstance().collection("Meetings").document(id).delete()
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun addDocument(meeting: Meeting): Completable = Completable.create { emitter ->
        withUserCredentials {
            FirebaseFirestore.getInstance().collection("Meetings").document(meeting.name).set(meeting.toMap())
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun addMember(name: String, position: String, meeting: Meeting): Completable = Completable.create { emitter ->
        val newMeeting = meeting.clone().apply { members.add(Member(name, position)) }
        withUserCredentials {
            FirebaseFirestore.getInstance().collection("Meetings").document(meeting.name).set(newMeeting.toMap())
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun search(query: String): Single<List<Meeting>> = Single.create { emitter ->
        withUserCredentials {
            FirebaseFirestore.getInstance().collection("Meetings").get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        it.documents.toMutableList().map {
                            it.toObject(MeetingApiResponse::class.java)!!.toDataClass()
                        }
                            .also { emitter.onSuccess(it.filter { it.description.contains(query) }) }
                    }
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    private fun meetingsSingle(): Single<List<Meeting>> = Single.create { emitter ->
        withUserCredentials {
            FirebaseFirestore.getInstance().collection("Meetings").get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        it.documents.toMutableList().map {
                            it.toObject(MeetingApiResponse::class.java)!!.toDataClass()
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

    private fun withUserCredentials(action: () -> Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { action() }
    }
}