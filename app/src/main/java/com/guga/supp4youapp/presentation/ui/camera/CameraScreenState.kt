
import androidx.lifecycle.MutableLiveData
import com.guga.supp4youapp.domain.model.AllowContinueType

data class CameraScreenState(
    val isContinueButtonEnabled: Boolean = false,
    val isTakePhotoButtonEnabled: Boolean = false,
    val shotsCount: Int = 0,
    val countDown: CountDownTimerState = CountDownTimerState(),
    val numberOfParticipants: Long = 1
)

data class CountDownTimerState(
    val type: CountDownTimerType = CountDownTimerType.INITIAL,
    val timer: Long = 0L
)

enum class CountDownTimerType {
    INITIAL,
    END_DATE,
    ALLOW_CONTINUE,
    DONE
}

val initialCameraScreenState = CameraScreenState()

fun MutableLiveData<CameraScreenState>.setState(
    block: CameraScreenState.() -> CameraScreenState
) {
    value?.let { value = block(it) }
}

fun CountDownTimerState.hasEndDatePassed(): Boolean {
    return when {
        type == CountDownTimerType.END_DATE && timer <= 0L -> true
        type != CountDownTimerType.END_DATE -> true
        else -> false
    }
}

fun CountDownTimerState.hasAllowContinuePassed(): Boolean {
    return when {
        type == CountDownTimerType.ALLOW_CONTINUE && timer == 0L -> true
        else -> false
    }
}

fun CameraScreenState.shouldEnableTakePhotoButton(remainingShots: Int): Boolean {
    return when {
        remainingShots == 0 -> false
        countDown.hasEndDatePassed() -> false
        else -> true
    }
}

fun CameraScreenState.shouldEnableContinueButton(
    allowContinueType: AllowContinueType
): Boolean {
    return when {
        countDown.hasAllowContinuePassed() -> true
        allowContinueType == AllowContinueType.During -> true
        allowContinueType == AllowContinueType.After && countDown.hasEndDatePassed() -> true
        else -> false
    }
}
