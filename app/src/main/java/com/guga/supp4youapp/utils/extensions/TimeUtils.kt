package com.guga.supp4youapp.utils.extensions

import android.os.CountDownTimer
import com.guga.supp4youapp.utils.Constants
import java.util.Date
import java.util.concurrent.TimeUnit

private const val TICK_TIME = 1000L // One second

fun startCountDownTimer(
    countFrom: Long,
    doOnTick: (Long) -> Unit,
    doOnFinish: () -> Unit
): CountDownTimer {
    return object : CountDownTimer(countFrom, TICK_TIME) {

        override fun onTick(millisUntilFinished: Long) {
            doOnTick(millisUntilFinished)
        }

        override fun onFinish() {
            doOnFinish()
            cancel()
        }
    }.start()
}

fun formatTimeAndDaysUntilDate(fromMillis: Long): String {
    val numberOfDays = TimeUnit.MILLISECONDS.toDays(fromMillis)
    val numberOfHours = ((fromMillis / (1000 * 60 * 60)) % 24)
    val numberOfMinutes = ((fromMillis / (1000 * 60)) % 60)
    val numberOfSeconds = (fromMillis / 1000) % 60

    val time = String.format(
        "%02d:%02d:%02d",
        numberOfHours,
        numberOfMinutes,
        numberOfSeconds
    )

    return if (numberOfDays != 0L) "$numberOfDays:$time" else time
}

fun now(): Long = Date().time

fun diffFromNow(timestamp: Long, plusHours: Long = 0): Long {
    val additionalTime = TimeUnit.HOURS.toMillis(plusHours)

    val formattedTimestamp = when {
        timestamp > Constants.MAX_SECONDS_TIMESTAMP_VALUE -> timestamp
        else -> timestamp * Constants.MILLISECONDS_MULTIPLIER
    }.let { Date(it).time }

    return formattedTimestamp + additionalTime - now()
}

// This is a function to test countdown configs and behavior
fun generateTimeDiffFromNow(
    additionalTime: Long = 15,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): Long {
    val futureTime = now() + timeUnit.toMillis(additionalTime)

    return futureTime - now()
}