package com.guga.supp4youapp.utils.extensions



import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import com.guga.supp4youapp.domain.model.Event

private const val EVENT_ARGS_KEY = "EVENT_ARGS_KEY"

fun Intent.putEventArgs(event: Event): Intent {
    return putExtra(EVENT_ARGS_KEY, event)
}

val Intent.eventArgs: Event?
    get() = getParcelableEvent()

private fun Intent.getParcelableEvent(): Event? {
    return if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(EVENT_ARGS_KEY, Event::class.java)
    } else {
        getParcelableExtra(EVENT_ARGS_KEY)
    }
}
