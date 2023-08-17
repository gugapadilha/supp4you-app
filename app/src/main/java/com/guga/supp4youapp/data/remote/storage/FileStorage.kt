package com.guga.supp4youapp.data.remote.storage

import com.guga.supp4youapp.domain.model.Event
import com.guga.supp4youapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import java.io.File

typealias DownloadURL = String

interface FileStorage {

    fun getFilesForEvent(
        event: Event,
        nodeID: String = Constants.DEFAULT_NODE_ID
    ): Flow<DownloadURL>

    suspend fun uploadFileForEvent(
        event: Event,
        file: File,
        pushID: String,
        nodeID: String = Constants.DEFAULT_NODE_ID
    ): DownloadURL

    suspend fun deleteFile(urlRef: DownloadURL)
}