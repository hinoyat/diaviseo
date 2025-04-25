package com.example.diaviseo.ui.component

// Material3 디자인 시스템의 컴포넌트들 (Scaffold, NavigationBar 등)
import androidx.compose.material3.*

// 우선 테스트를 위해 기본 제공 아이콘으로 제작 -> 추후에 목업에 사용한 아이콘으로 변경
// Jetpack Compose 기본 제공 아이콘 모음
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// 컴포저블 함수로 UI 구성할 때 사용
import androidx.compose.runtime.Composable

// 화면 전환을 위한 Navigation 컨트롤러
import androidx.navigation.NavHostController

// 여백(padding)이나 정렬을 위한 레이아웃 관련 도구
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Composable 함수: 하단바 UI를 그려주는 함수
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // 하단 네비게이션 바 영역
    NavigationBar (
        tonalElevation = 10.dp // 그림자(입체감) 높이 설정
    ){
        // 네비게이션 아이템 1: 홈
        NavigationBarItem(
            icon={Icon(Icons.Default.Home, contentDescription = "홈")}, // 아이콘 설정
            label = { Text("홈")},// 텍스트 라벨
            selected = false, // 현재 선택된 탭 여부 (아직 고정 false)
            onClick = {/* 클릭 시 이동 처리 예정 */}
        )
        // 네비게이션 아이템 2: 챗봇
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "챗봇") },
            label = { Text("챗봇") },
            selected = false,
            onClick = { /* 나중에 채팅 화면으로 이동 */ }
        )

        // 네비게이션 아이템 3: 등록
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "등록") },
            label = { Text("등록") },
            selected = false,
            onClick = { /* 나중에 등록 화면으로 이동 */ }
        )

        // 네비게이션 아이템 4: 목표
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "목표") },
            label = { Text("목표") },
            selected = false,
            onClick = { /* 나중에 목표 화면으로 이동 */ }
        )

        // 네비게이션 아이템 5: 마이페이지
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "마이") },
            label = { Text("마이") },
            selected = false,
            onClick = { /* 마이페이지 화면으로 이동 */ }
        )
    }
}

