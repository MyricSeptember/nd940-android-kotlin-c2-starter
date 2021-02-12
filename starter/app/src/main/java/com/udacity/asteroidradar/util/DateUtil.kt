package com.udacity.asteroidradar.util

import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.ROOT)

private val defaultTimeZone = TimeZone.getDefault()

fun getToday(): Date = Calendar.getInstance().time

fun addDaysToCurrentDate(days: Int): Date = with(Calendar.getInstance()) {
    add(Calendar.DAY_OF_YEAR, days)
    return@with time
}

val Date.formattedDate: String
    get() = dateFormat.apply { timeZone = defaultTimeZone }.format(this)
