
package com.meetingsprod.meetings.main.utils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.data.pojo.Meeting

class MeetingsAdapter(val action: (String) -> Unit) : RecyclerView.Adapter<MeetingsAdapter.ViewHolder>() {
    var data: MutableList<Meeting> = ArrayList()
        set(value) {
            this.data.clear()
            this.data.addAll(value)
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_meeting,
                parent,
                false
            )
        )

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
        viewHolder.itemView.setOnClickListener { action(data[position].name) }
    }

    override fun getItemCount(): Int = data.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val startDate = itemView.findViewById<TextView>(R.id.tvStartDate)
        val endDate = itemView.findViewById<TextView>(R.id.tvEndDate)

        fun bind(meeting: Meeting) {
            name.text = itemView.context.getString(R.string.meeting_name, meeting.name)
            startDate.text = itemView.context.getString(R.string.meeting_startDate, meeting.startDate)
            endDate.text = itemView.context.getString(R.string.meeting_endDate, meeting.endDate)
        }
    }
}