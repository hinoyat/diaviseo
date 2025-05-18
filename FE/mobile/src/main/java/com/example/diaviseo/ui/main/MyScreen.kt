package com.example.diaviseo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.network.user.dto.req.UserUpdateRequest
import com.example.diaviseo.network.user.dto.res.FetchProfileResponse
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.components.my.*
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.semibold16
import com.example.diaviseo.viewmodel.ProfileViewModel

@Composable
fun MyScreen(navController: NavHostController) {
    val profileViewModel: ProfileViewModel = viewModel()
    val profile by profileViewModel.myProfile.collectAsState()

    // 최초 1회 프로필 로드
    LaunchedEffect(Unit) {
        profileViewModel.fetchMyProfile()
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
            ) {
                CommonTopBar(
                    onLeftActionClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() // ✅ FAQ 잘리는 현상 방지
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HeaderSection(
                    userName = profile?.nickname ?: "불러오는 중",
                    navController = navController
                    )
            }
            item {
                ProfileSection(navController, profile)
            }
            item {
                HealthSection(navController)
            }
            item {
                SyncSection(navController)
            }
            item {
                SettingSection(
                    isAlarmEnabled = profile?.notificationEnabled ?: true,
                    onToggle = {
                        profileViewModel.updateUserProfile(
                            UserUpdateRequest(notificationEnabled = it)
                        )
                    },
                )
            }
            item {
                CustomerSection(
                    onFaqClick = { navController.navigate("faq") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(55.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(
    userName: String,
    navController: NavHostController
) {
    MyHeaderSection(
        userName = userName,
        navController = navController
    )
}

@Composable
private fun ProfileSection(navController: NavHostController, profile: FetchProfileResponse?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MyPhysicalInfoCard(
            height = profile?.height ?: 0.0,
            weight =  profile?.weight ?: 0.0,
            onEditClick = {
                navController.navigate("edit_physical_info")
            }
        )
    }
}

@Composable
private fun HealthSection(navController: NavHostController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MyHealthDataList(
            onEditAllergy = { navController.navigate("edit_allergy") },
            onEditDisease = { navController.navigate("edit_disease") }
        )
        // 선호하는 운동 제거 (운동 - 즐겨찾기 기능으로 대체)
//        MyExtraDataList(
//            onEditExercise = { navController.navigate("edit_exercise") }
//        )
    }
}

@Composable
private fun SyncSection(navController: NavHostController) {
    MySyncSection(
        onConnectClick = {
            navController.navigate("healthConnect/manage")
        },

    )
}

@Composable
private fun SettingSection(
    isAlarmEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Text(
        text = "설정 관리",
        style = semibold16,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MyAlarmSettingCard(
            isEnabled = isAlarmEnabled,
            onToggle = onToggle
        )
    }
}

@Composable
fun CustomerSection (
    onFaqClick: () -> Unit
) {
    Text(
        text = "고객 관리",
        style = semibold16,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    MyFaqCard(onClick = onFaqClick)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyScreenPreview() {
    val navController = rememberNavController()
    MyScreen(navController = navController)
}
