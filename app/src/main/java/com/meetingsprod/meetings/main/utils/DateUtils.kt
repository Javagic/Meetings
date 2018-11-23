/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 21.11.18 14:38
 */

package com.meetingsprod.meetings.main.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.meetingsprod.meetings.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun AppCompatActivity.showDatePickerDialog(
    listener: DateTimePickerListener,
    editText: EditText,
    calendar: Calendar = Calendar.getInstance()
) =
    DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            listener.onDateSet(calendar.time, editText)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
        .apply { setCancelable(false) }
        .apply { datePicker.minDate = Calendar.getInstance().timeInMillis }
        .show()

fun AppCompatActivity.showTimePickerDialog(
    listener: DateTimePickerListener,
    editText: EditText,
    calendar: Calendar = Calendar.getInstance()
) =
    TimePickerDialog(
        this,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            listener.onTimeSet(calendar.time, editText)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
        .apply { setCancelable(false) }
        .show()


interface DateTimePickerListener {
    fun onDateSet(date: Date, editText: EditText)

    fun onTimeSet(date: Date, editText: EditText)
}

val Context.dateTimeFormat: SimpleDateFormat
    get() =
        SimpleDateFormat(getString(R.string.date_time_format_long_year), Locale("RU"))

val Context.dateFormat: SimpleDateFormat
    get() =
        SimpleDateFormat(getString(R.string.date_format), Locale("RU"))
val Context.timeFormat: SimpleDateFormat
    get() =
        SimpleDateFormat(getString(R.string.time_format), Locale("RU"))

fun DateFormat.parseOrNull(date: String): Date? = try {
    this.parse(date)
} catch (e: ParseException) {
    e.printStackTrace()
    null
}
