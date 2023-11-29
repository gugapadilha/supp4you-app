package com.guga.supp4youapp.presentation.ui.camera
import CameraScreenState
import CountDownTimerType
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guga.supp4youapp.domain.model.Event
import com.guga.supp4youapp.domain.usecase.EnqueueNotificationUseCase
import com.guga.supp4youapp.domain.usecase.GetCountDownTimerUseCase
import com.guga.supp4youapp.domain.usecase.GetNumberOfParticipantsUseCase
import com.guga.supp4youapp.domain.usecase.UploadFileUseCase
import com.guga.supp4youapp.presentation.ui.camera.model.CameraActions
import com.guga.supp4youapp.utils.livedata.SingleLiveEvent
import initialCameraScreenState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import setState
import shouldEnableContinueButton
import shouldEnableTakePhotoButton
import java.io.File

class CameraViewModel(
    private val uploadFileUseCase: UploadFileUseCase,
    private val getNumberOfParticipantsUseCase: GetNumberOfParticipantsUseCase,
    private val getCountDownTimerUseCase: GetCountDownTimerUseCase,
    private val enqueueNotificationUseCase: EnqueueNotificationUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val mutableState = MutableLiveData(initialCameraScreenState)
    val state: LiveData<CameraScreenState> = mutableState

    private val actionDispatcher = SingleLiveEvent<CameraActions>()
    val action: LiveData<CameraActions> = actionDispatcher

    private lateinit var currentEvent: Event

    fun setCurrentEvent(event: Event) {
        currentEvent = event

        getNumberOfParticipants()
        getCountDownTimer()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun uploadFile(file: File) {
        GlobalScope.launch {
            uploadFileUseCase(currentEvent, file)
                .flowOn(dispatcher)
                .onStart {
                    state.value?.shotsCount?.also { configTakePhotoButtonAndShotsCount(it.dec()) }
                }
        }
    }

    private fun configTakePhotoButtonAndShotsCount(shotsCount: Int) {
        mutableState.setState {
            copy(
                shotsCount = shotsCount,
                isTakePhotoButtonEnabled = shouldEnableTakePhotoButton(shotsCount)
            )
        }
    }

    private fun getNumberOfParticipants() {
        viewModelScope.launch {
            getNumberOfParticipantsUseCase(currentEvent)
                .collect { mutableState.setState { copy(numberOfParticipants = it) } }
        }
    }

    private fun getCountDownTimer() {
        mutableState.value = state.value?.copy(isTakePhotoButtonEnabled = true)
        enqueueNotificationUseCase(currentEvent.endDate, currentEvent.allowContinue.plusTime)
        getCountDownTimerUseCase(
            currentEvent.endDate,
            onTick = { updateTimerState(it, CountDownTimerType.END_DATE) },
            onFinish = {
                mutableState.value = state.value?.copy(isTakePhotoButtonEnabled = false)
                changeToAllowContinueCountDown()
            }
        )
    }

    private fun changeToAllowContinueCountDown() {
        getCountDownTimerUseCase(
            currentEvent.endDate,
            extraTime = currentEvent.allowContinue.plusTime,
            onTick = { updateTimerState(it, CountDownTimerType.ALLOW_CONTINUE) },
            onFinish = {
                mutableState.setState {
                    copy(
                        isContinueButtonEnabled = true,
                        countDown = countDown.copy(type = CountDownTimerType.DONE)
                    )
                }
            }
        )
    }

    fun onTakeShotButtonClicked() {
        val isTakePhotoButtonEnabled = state.value?.isTakePhotoButtonEnabled
        val countDownType = state.value?.countDown?.type

        val action = when {
            isTakePhotoButtonEnabled == true -> CameraActions.TakeShot
            countDownType == CountDownTimerType.DONE -> {
                CameraActions.ShowShotsDisabledToast(false)
            }
            else -> CameraActions.ShowShotsDisabledToast(true)
        }

        actionDispatcher.value = action
    }

    private fun updateTimerState(time: Long, type: CountDownTimerType) {
        mutableState.setState {
            copy(
                countDown = countDown.copy(timer = time, type = type),
                isContinueButtonEnabled = shouldEnableContinueButton(currentEvent.allowContinue)
            )
        }
    }
}