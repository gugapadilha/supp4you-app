import android.os.Build
import com.google.firebase.FirebaseApp
import com.guga.supp4youapp.R
import com.guga.supp4youapp.presentation.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MainActivityTest {
    private var activity: MainActivity? = null

    @Before
    fun setUp() {
        // Inicialize o Firebase sombreado
        // Arrange
        FirebaseApp.initializeApp(RuntimeEnvironment.application)

        //Act
        // Crie uma instância da MainActivity
        activity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    fun testActivityTheme() {
        // Arrange

        //Act
        // Verifique se o tema da atividade está correto
        val themeResId = Shadows.shadowOf(activity).callGetThemeResId()

        //Assert
        assertEquals(R.style.Theme_POV, themeResId)
    }

}
