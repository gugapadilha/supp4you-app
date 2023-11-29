package com.guga.supp4youapp.domain.usecase

import com.guga.supp4youapp.domain.model.Event
import com.guga.supp4youapp.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetNumberOfParticipantsUseCase(
    private val eventRepository: EventRepository
) {
    operator fun invoke(event: Event): Flow<Long> {
        return eventRepository.getEventNumberParticipants(event)
    }
}