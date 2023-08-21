package com.guga.supp4youapp.utils.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    //    override fun doWork(): Result {
////        val notification = GalleryNotification(appContext)
//
////        return notification.dispatch().fold(
////            onSuccess = { Result.success() },
////            onFailure = {
////                it.logError("Failed to dispatch gallery notification")
////                Result.failure()
//          }
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}