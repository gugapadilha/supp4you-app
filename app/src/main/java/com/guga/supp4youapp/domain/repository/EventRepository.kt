package com.untitledshows.pov.domain.repository

import com.guga.supp4youapp.domain.model.Event
import com.guga.supp4youapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun saveCurrentEventId(id: String)
    fun getCurrentEventId(): String
    fun getCurrentEventResume(queryValue: String): Flow<Event>
    fun registerUserInEvent(user: User, event: Event): Flow<Unit>
    fun getEventNumberParticipants(event: Event): Flow<Long>
}