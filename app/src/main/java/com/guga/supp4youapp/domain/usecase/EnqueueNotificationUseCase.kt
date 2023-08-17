package com.guga.supp4youapp.domain.usecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.guga.supp4youapp.utils.extensions.diffFromNow
import com.guga.supp4youapp.utils.worker.NotificationWorker
import java.util.concurrent.TimeUnit
private const val WORK_TAG = "Notification_Gallery_Work"
class EnqueueNotificationUseCase(private val appContext: Context) {

    operator fun invoke(
        eventCloseTime: Double,
        galleryOpenExtraTime: Long
    ) {
        val diffTime = diffFromNow(eventCloseTime.toLong(), galleryOpenExtraTime)

        if (diffTime <= 0) return

        val work = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(diffTime, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(appContext).enqueue(work)
    }
}