package com.example.diaviseo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect

import com.example.diaviseo.ui.signup.SignupNavGraph
import com.example.diaviseo.ui.components.TransparentStatusBar
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color

import com.example.diaviseo.ui.main.MainScreen
import com.example.diaviseo.ui.theme.DiaViseoTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        TransparentStatusBar(window)

        setContent {
            DiaViseoTheme {
            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = true // 글씨와 아이콘을 검은색으로
                )
            }
            TransparentStatusBar(window) // setContent {} 안에서 호출
            // 회원가입 & 로그인 로직 구현 이후
            // 로그인, 회원가입된 사용자 -> MainScreen으로
            // 회원가입해야하는 신규 유저 -> SignupNavGraph로 이동하도록 수정 필요

            MainScreen() 
            SignupNavGraph(navController)
        }
    }
    }
}
