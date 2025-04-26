package com.example.diaviseo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.diaviseo.ui.signup.SignupNavGraph
import com.example.diaviseo.ui.components.TransparentStatusBar
import androidx.navigation.compose.rememberNavController

import com.example.diaviseo.ui.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TransparentStatusBar(window)

        setContent {
            MainScreen()
            val navController = rememberNavController()
            SignupNavGraph(navController)
        }
    }
}
