/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 13.11.18 18:49
 */

package com.meetingsprod.meetings.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.data.pojo.Meeting

class MeetingsAdapter : RecyclerView.Adapter<MeetingsAdapter.ViewHolder>() {
    var data: MutableList<Meeting> = ArrayList()
        set(value) {
            this.data.clear()
            this.data.addAll(value)
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_meeting, parent, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)

        fun bind(meeting: Meeting) {
            name.text = meeting.name
        }
    }
}