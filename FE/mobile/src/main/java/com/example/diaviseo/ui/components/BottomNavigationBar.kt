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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.ui.theme.DiaViseoColors

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    isFabMenuOpen: MutableState<Boolean>
) {
    // NavController의 백스택을 관찰해서 현재 route를 가져옵니다.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .navigationBarsPadding()
    ) {
        Surface(
            tonalElevation = 0.dp,
            shadowElevation = 4.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                fun navTo(route: String) {
                    if (currentRoute == route) return

                    // 1) Home 으로 돌아갈 땐 popBackStack
                    val popped = navController.popBackStack(route, inclusive = false)
                    if (!popped) {
                        // 2) 백스택에 route 가 없으면 navigate
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (currentRoute == "home")
                                    R.drawable.bottom_active_dashboard
                                else
                                    R.drawable.bottom_dashboard
                            ),
                            contentDescription = "홈",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("홈", style = medium12) },
                    selected = currentRoute == "home",
                    onClick = { navTo("home") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (currentRoute == "chat")
                                    R.drawable.bottom_active_chat
                                else
                                    R.drawable.bottom_chat
                            ),
                            contentDescription = "챗봇",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("챗봇", style = medium12) },
                    selected = currentRoute == "chat",
                    onClick = { navTo("chat") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )

                Spacer(modifier = Modifier.width(56.dp))

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (currentRoute == "goal")
                                    R.drawable.bottom_active_goal
                                else
                                    R.drawable.bottom_goal
                            ),
                            contentDescription = "평가",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("평가", style = medium12) },
                    selected = currentRoute == "goal",
                    onClick = { navTo("goal") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedTextColor = Color(0xFF1673FF),
                        unselectedTextColor = Color(0xFFBDBDBD)
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (currentRoute == "my")
                                    R.drawable.bottom_active_my
                                else
                                    R.drawable.bottom_my
                            ),
                            contentDescription = "마이",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text("마이", style = medium12) },
                    selected = currentRoute == "my",
                    onClick = { navTo("my") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
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