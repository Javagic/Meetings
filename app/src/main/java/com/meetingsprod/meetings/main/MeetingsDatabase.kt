/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 13.11.18 21:10
 */

package com.meetingsprod.meetings.main

import android.arch.persistence.room.Database


@Database(entities = [Task::class, TaskInfo::class, GeometryEntity::class, StreetProgress::class],
    version = 1)
class MeetingsDatabase()