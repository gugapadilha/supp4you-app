package com.guga.supp4youapp.domain.usecase

import com.guga.supp4youapp.utils.extensions.diffFromNow
import com.guga.supp4youapp.utils.extensions.startCountDownTimer


class GetCountDownTimerUseCase {

    operator fun invoke(
        targetDate: Double,
        extraTime: Long? = null,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
       val dateTimeDiff = diffFromNow(
           timestamp = targetDate.toLong(),
           plusHours = extraTime ?: 0
       )

        startCountDownTimer(dateTimeDiff, onTick, onFinish)
    }
}