package com.centennial.donateblood.utils

import android.content.Context
import androidx.annotation.Nullable
import com.centennial.donateblood.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TimeAgo {

    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var dateFormat: SimpleDateFormat
    lateinit var timeFormat: DateFormat
    lateinit var dateTimeNow: Date
    lateinit var timeFromData: String
    lateinit var pastDate: String
    internal var sDateTimeNow: String

    @Nullable
    internal var context: Context? = null
    //private static final int YEARS_MILLIS = 12 * MONTHS_MILLIS;

    init {

        simpleDateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        dateFormat = SimpleDateFormat("dd/MM/yyyy")
        timeFormat = SimpleDateFormat("h:mm aa")

        val now = Date()
        sDateTimeNow = simpleDateFormat.format(now)

        try {
            dateTimeNow = simpleDateFormat.parse(sDateTimeNow)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    fun locale(context: Context): TimeAgo {
        this.context = context
        return this
    }

    fun with(simpleDateFormat: SimpleDateFormat): TimeAgo {
        this.simpleDateFormat = simpleDateFormat
        this.dateFormat =
            SimpleDateFormat(simpleDateFormat.toPattern().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        this.timeFormat =
            SimpleDateFormat(simpleDateFormat.toPattern().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        return this
    }

    fun getTimeAgo(startDate: Date): String {

        //  date counting is done till todays date
        val endDate = dateTimeNow

        //  time difference in milli seconds
        val different = endDate.time - startDate.time

        if (context == null) {
            if (different < MINUTE_MILLIS) {
                return context!!.resources.getString(R.string.just_now)
            } else if (different < 2 * MINUTE_MILLIS) {
                return context!!.resources.getString(R.string.a_min_ago)
            } else if (different < 50 * MINUTE_MILLIS) {
                return (different / MINUTE_MILLIS).toString() + context!!.getString(R.string.mins_ago)
            } else if (different < 90 * MINUTE_MILLIS) {
                return context!!.getString(R.string.a_hour_ago)
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = timeFormat.format(startDate)
                return timeFromData
            } else if (different < 48 * HOUR_MILLIS) {
                return context!!.getString(R.string.yesterday)
            } else if (different < 7 * DAY_MILLIS) {
                return (different / DAY_MILLIS).toString() + context!!.getString(R.string.days_ago)
            } else if (different < 2 * WEEKS_MILLIS) {
                return (different / WEEKS_MILLIS).toString() + context!!.getString(R.string.week_ago)
            } else if (different < 3.5 * WEEKS_MILLIS) {
                return (different / WEEKS_MILLIS).toString() + context!!.getString(R.string.weeks_ago)
            } else {
                pastDate = dateFormat.format(startDate)
                return pastDate
            }
        } else {
            if (different < MINUTE_MILLIS) {
                return context!!.resources.getString(R.string.just_now)
            } else if (different < 2 * MINUTE_MILLIS) {
                return context!!.resources.getString(R.string.a_min_ago)
            } else if (different < 50 * MINUTE_MILLIS) {
                return (different / MINUTE_MILLIS).toString() + context!!.getString(R.string.mins_ago)
            } else if (different < 90 * MINUTE_MILLIS) {
                return context!!.getString(R.string.a_hour_ago)
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = timeFormat.format(startDate)
                return timeFromData
            } else if (different < 48 * HOUR_MILLIS) {
                return context!!.getString(R.string.yesterday)
            } else if (different < 7 * DAY_MILLIS) {
                return (different / DAY_MILLIS).toString() + context!!.getString(R.string.days_ago)
            } else if (different < 2 * WEEKS_MILLIS) {
                return (different / WEEKS_MILLIS).toString() + context!!.getString(R.string.week_ago)
            } else if (different < 3.5 * WEEKS_MILLIS) {
                return (different / WEEKS_MILLIS).toString() + context!!.getString(R.string.weeks_ago)
            } else {
                pastDate = dateFormat.format(startDate)
                return pastDate
            }
        }
    }

    companion object {

        private val SECOND_MILLIS = 1000
        private val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private val DAY_MILLIS = 24 * HOUR_MILLIS
        private val WEEKS_MILLIS = 7 * DAY_MILLIS
        private val MONTHS_MILLIS = 4 * WEEKS_MILLIS
    }
}