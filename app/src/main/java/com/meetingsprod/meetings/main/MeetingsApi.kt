/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 11.11.18 19:44
 */

package com.meetingsprod.meetings.main


interface MeetingsApi {
    fun getMeetings(): List<Meeting>
    fun checkIn(): List<Meeting>
    fun getMeetingDetails(): List<Meeting>
    fun cancel(): List<Meeting>
    fun createMeeting(): List<Meeting>
    fun searchMeeting(): List<Meeting>
}