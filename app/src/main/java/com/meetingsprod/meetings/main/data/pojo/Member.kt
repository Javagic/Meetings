/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 11.11.18 19:48
 */

package com.meetingsprod.meetings.main.data.pojo

data class Member(var name: String = "", var position: String = "") {
    fun toMap() = HashMap<String, String>().apply {
        put("name", name)
        put("position", position)
    }

    fun clone() = Member(name, position)
}