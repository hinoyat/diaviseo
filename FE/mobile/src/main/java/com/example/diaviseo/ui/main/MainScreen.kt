package com.example.diaviseo.ui.main

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.ui.component.BottomNavigationBar
import androidx.compose.foundation.layout.padding
@Composable
fun MainScreen() {
    // 화면 이동을 관리해주는 내비게이션 컨트롤러
    val navController = rememberNavController()

    // Scaffold 를 사용해 하단바 포함한 기본 화면 구성
    Scaffold (
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ){
        innerPadding ->
        // 임시로 가운데 텍스트 표시
        Text (
            text= "하단바가 잘 나올까요?",
            modifier = Modifier.padding(innerPadding)
        )
    }
}