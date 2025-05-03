package com.example.diaviseo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.diaviseo.R
import androidx.compose.ui.text.font.FontWeight

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    isFabMenuOpen: MutableState<Boolean>
) {
    var selectedTab by remember { mutableStateOf("dashboard") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Surface(
            tonalElevation = 0.dp,
            shadowElevation = 4.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
        ) {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "dashboard") R.drawable.bottom_active_dashboard else R.drawable.bottom_dashboard
                            ),
                            contentDescription = "대시보드",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("대시보드", fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "dashboard",
                    onClick = { selectedTab = "dashboard" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "chat") R.drawable.bottom_active_chat else R.drawable.bottom_chat
                            ),
                            contentDescription = "챗봇",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("챗봇", fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "chat",
                    onClick = { selectedTab = "chat" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )

                Spacer(modifier = Modifier.width(56.dp))

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "goal") R.drawable.bottom_active_goal else R.drawable.bottom_goal
                            ),
                            contentDescription = "목표",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("목표", fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "goal",
                    onClick = { selectedTab = "goal" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedTab == "my") R.drawable.bottom_active_my else R.drawable.bottom_my
                            ),
                            contentDescription = "마이",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("마이", fontWeight = FontWeight.Normal) },
                    selected = selectedTab == "my",
                    onClick = { selectedTab = "my" },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )
            }
        }

        FloatingActionButton(
            onClick = { isFabMenuOpen.value = !isFabMenuOpen.value },
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)
                .size(56.dp)
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