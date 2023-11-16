package com.guga.supp4youapp.presentation.ui.camera

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CameraActivityTest {

    private lateinit var cameraActivity: CameraActivity

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var viewModelProvider: ViewModelProvider

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        MockitoAnnotations.initMocks(this)

    }

    @Test
    fun testTakePhoto() {
        MockitoAnnotations.initMocks(this)

        val activity = Robolectric.buildActivity(CameraActivity::class.java).create().start().get()

        // Verificar se isPhotoBeingTaken é falso antes de chamar a função
        assert(!activity.isPhotoBeingTaken)

        // Chamar a função takePhoto()
        activity.takePhoto("enteredToken")

        // Verificar se isPhotoBeingTaken é verdadeiro após a chamada da função
        assert(activity.isPhotoBeingTaken)
    }
}
