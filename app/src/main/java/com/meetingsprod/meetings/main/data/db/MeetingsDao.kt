/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 14.11.18 20:04
 */

package com.meetingsprod.meetings.main.data.db

import android.arch.persistence.room.*
import com.meetingsprod.meetings.main.data.pojo.Meeting
import io.reactivex.Flowable

@Dao
abstract class MeetingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(type: Meeting): Long

    @Update
    abstract fun update(type: Meeting)

    @Delete
    abstract fun delete(type: Meeting)

    @Query("SELECT * FROM Meetings")
    abstract fun allFlowable(): Flowable<List<Meeting>>

    @Query("SELECT * FROM Meetings")
    abstract fun all(): List<Meeting>

    @Query("SELECT * FROM Meetings WHERE name = :name")
    abstract fun get(name: String): Meeting?
}