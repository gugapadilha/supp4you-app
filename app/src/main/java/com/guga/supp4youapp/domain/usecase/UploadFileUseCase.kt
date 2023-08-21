package com.guga.supp4youapp.domain.usecase

import SessionRepository
import SubmissionsRepository
import com.guga.supp4youapp.data.remote.storage.DownloadURL
import com.guga.supp4youapp.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.io.File

class UploadFileUseCase(
    private val submissionsRepository: SubmissionsRepository,
    private val sessionRepository: SessionRepository
) {

    operator fun invoke(event: Event, file: File): Flow<DownloadURL> {
        return submissionsRepository.submitFileForEvent(
            event = event,
            user = sessionRepository.getCurrentUserOrThrow(),
            file = file
        )
    }
}