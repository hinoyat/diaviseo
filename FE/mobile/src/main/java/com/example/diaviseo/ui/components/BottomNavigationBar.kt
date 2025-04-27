package com.example.diaviseo.ui.components

// Material3 디자인 시스템의 컴포넌트들 (Scaffold, NavigationBar 등)
import androidx.compose.material3.*

// 우선 테스트를 위해 기본 제공 아이콘으로 제작 -> 추후에 목업에 사용한 아이콘으로 변경
// Jetpack Compose 기본 제공 아이콘 모음

// 컴포저블 함수로 UI 구성할 때 사용
import androidx.compose.runtime.Composable

// 화면 전환을 위한 Navigation 컨트롤러
import androidx.navigation.NavHostController

// 여백(padding)이나 정렬을 위한 레이아웃 관련 도구
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diaviseo.R

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight

// Composable 함수: 하단바 UI를 그려주는 함수
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // 현재 선택된 탭을 기억하는 상태 변수
    var selectedTab by remember { mutableStateOf("dashboard") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // 하단바 (Surface + NavigationBar)
        Surface(
            tonalElevation = 0.dp, // 색조 섞임 방지
            shadowElevation = 4.dp, // 그림자
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // 하단바 높이
                .align(Alignment.BottomCenter) // Surface는 하단에 고정
        ) {
            // 하단 네비게이션 바 영역
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp // material3 색조 자동 섞임 방지
            ) {
                // 네비게이션 아이템 1: 대시보드
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab=="dashboard")
                                    R.drawable.bottom_active_dashboard
                                    else
                                    R.drawable.bottom_dashboard),
                            contentDescription = "대시보드",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }, // 아이콘 설정
                    label = {     Text(
                        "대시보드",
                        fontWeight = FontWeight.Normal
                    ) },// 텍스트 라벨
                    selected = selectedTab == "dashboard",
                    onClick = { selectedTab = "dashboard" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,          // ✅ 배경 파랑 없애기!
                        selectedIconColor = Color.Unspecified,       // 선택된 아이콘 색 유지
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),            // 선택된 텍스트 색
                        unselectedTextColor = Color(0xFFBDBDBD)             // 비선택 텍스트 색
                    )
                )
                // 네비게이션 아이템 2: 챗봇
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "chat")
                                    R.drawable.bottom_active_chat
                                else
                                    R.drawable.bottom_chat),
                            contentDescription = "챗봇",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text(
                                "챗봇",
                                fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "chat",
                    onClick = { selectedTab = "chat" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,          // ✅ 배경 파랑 없애기!
                        selectedIconColor = Color.Unspecified,       // 선택된 아이콘 색 유지
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),             // 선택된 텍스트 색
                        unselectedTextColor = Color(0xFFBDBDBD)           // 비선택 텍스트 색
                    )
                )

                Spacer(modifier = Modifier.width(56.dp)) // 가운데 버튼 공간 벌려놓기

                // 네비게이션 아이템 3: 목표
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "goal")
                                        R.drawable.bottom_active_goal
                                    else
                                        R.drawable.bottom_goal),
                            contentDescription = "목표",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text(
                                "목표",
                                fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "goal",
                    onClick = { selectedTab = "goal"},
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,          // ✅ 배경 파랑 없애기!
                        selectedIconColor = Color.Unspecified,       // 선택된 아이콘 색 유지
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),             // 선택된 텍스트 색
                        unselectedTextColor = Color(0xFFBDBDBD)        // 비선택 텍스트 색
                    )
                )

                // 네비게이션 아이템 4: 마이페이지
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "my")
                                        R.drawable.bottom_active_my
                                    else
                                        R.drawable.bottom_my),
                            contentDescription = "마이",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text(
                                "마이",
                                fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "my",
                    onClick = { selectedTab = "my" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,          // ✅ 배경 파랑 없애기!
                        selectedIconColor = Color.Unspecified,       // 선택된 아이콘 색 유지
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),             // 선택된 텍스트 색
                        unselectedTextColor = Color(0xFFBDBDBD)            // 비선택 텍스트 색
                    )
                )
            }
        }
            // 가운데 플로팅 버튼
            FloatingActionButton(
                onClick = {/* 등록 화면 이동 */ },
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp
                ), // 🔵 모든 상황 elevation 0으로 고정 (그림자 제거)
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20.dp)) // 위로 띄우기
                    .size(56.dp) // 플러스 버튼 크기
                    .navigationBarsPadding()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bottom_add),
                    contentDescription = "등록",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(50.dp)
                )
            }

    }
}
