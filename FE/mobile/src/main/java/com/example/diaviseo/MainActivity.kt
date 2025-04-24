package com.example.diaviseo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main) 썼으면
//        레이아웃에서 activity_main 쓰는건데 지금 우린 jetpack compose 사용
        setContent {
            Text("Hello S206!")
        }
    }
}
