package com.guga.supp4youapp.presentation.ui.adapter

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class CustomSpinnerAdapterTest{

    private lateinit var context: Context
    private lateinit var items: List<String>
    private lateinit var adapter: CustomSpinnerAdapter

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        context = mock(Context::class.java)
        items = listOf("Item 1", "Item 2", "Item 3")
    }

    @Test
    fun testItemCount() {
        //Assert
        assertNotNull(items.size)
    }
}