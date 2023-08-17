import android.app.LauncherActivity
import com.guga.supp4youapp.data.remote.database.EventFile
import com.guga.supp4youapp.data.remote.storage.DownloadURL
import com.guga.supp4youapp.domain.model.Event
import com.guga.supp4youapp.domain.model.User
import com.guga.supp4youapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SubmissionsRepository {

    fun listFilesForEvent(eventID: String): Flow<List<EventFile>>

    fun submitFileForEvent(
        event: Event,
        user: User,
        file: File
    ): Flow<DownloadURL>

    fun getSubmissionsCount(
        userID: String,
        eventID: String,
        nodeID: String = Constants.DEFAULT_NODE_ID
    ): Flow<Int>

    fun deleteFile(file: LauncherActivity.ListItem, eventID: String): Flow<Unit>
}