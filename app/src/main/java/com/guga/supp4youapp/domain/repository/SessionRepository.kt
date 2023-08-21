import com.guga.supp4youapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserOrThrow(): User
    fun signIn(googleIdToken: String): Flow<User>
    fun signOut()

    fun isUserSubscribedToNotifications(setValue: Boolean? = null): Boolean
}