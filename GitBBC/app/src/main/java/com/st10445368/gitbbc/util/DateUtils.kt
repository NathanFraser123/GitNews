package com.st10445368.gitbbc.util


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {

    /**
     * Calculates an ISO formatted date string (YYYY-MM-DD) relative to today.
     * e.g., getEarliestPublishDate(30) returns the date 30 days ago.
     */
    fun getEarliestPublishDate(daysAgo: Int = 30): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }
}