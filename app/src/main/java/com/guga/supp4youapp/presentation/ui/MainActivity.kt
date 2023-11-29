package com.guga.supp4youapp.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guga.supp4youapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_POV)
        setContentView(R.layout.activity_main)

    }
}