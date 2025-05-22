package com.example.diaviseo.ui.mypageedit.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.R
import com.example.diaviseo.network.user.dto.req.UserUpdateRequest
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.mypageedit.bottomsheet.EditBirthDateBottomSheet
import com.example.diaviseo.ui.mypageedit.bottomsheet.EditNicknameBottomSheet
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.AuthViewModel
import com.example.diaviseo.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileEditScreen(
    navController: NavHostController,
    rootNavController: NavHostController
) {
    val nicknameSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val birthDateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showNicknameSheet by remember { mutableStateOf(false) }
    var showBirthDateSheet by remember { mutableStateOf(false) }

    val profileViewModel: ProfileViewModel = viewModel()
    val profile by profileViewModel.myProfile.collectAsState()

    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )


    LaunchedEffect(Unit) {
        profileViewModel.fetchMyProfile()
    }

    if (showNicknameSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            onDismissRequest = { showNicknameSheet = false },
            sheetState = nicknameSheetState
        ) {
            EditNicknameBottomSheet(
                initialNickname = profile?.nickname ?: "",
                onSave = { newName ->
                    profileViewModel.updateUserProfile(
                        request = UserUpdateRequest(nickname = newName),
                        onSuccess = { showNicknameSheet = false },
                        onError = { msg -> Log.e("EditNickname", "수정 실패: $msg") }
                    )
                },
                onDismiss = { showNicknameSheet = false }
            )

        }
    }

    if (showBirthDateSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            onDismissRequest = { showBirthDateSheet = false },
            sheetState = birthDateSheetState
        ) {
            EditBirthDateBottomSheet(
                initialBirthDate = profile?.birthday?.replace("-", ".") ?: "",
                onSave = { input ->
                    val isoFormatted = input.replace(".", "-")
                    profileViewModel.updateUserProfile(
                        request = UserUpdateRequest(birthday = isoFormatted),
                        onSuccess = { showBirthDateSheet = false },
                        onError = { msg -> Log.e("EditBirth", "수정 실패: $msg") }
                    )
                },
                onDismiss = { showBirthDateSheet = false }
            )

        }
    }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("회원 탈퇴") },
            text = { Text("정말로 회원 탈퇴하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    authViewModel.deleteUser(
                        onSuccess = {
                            Toast.makeText(context, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            rootNavController.navigate("signupGraph") {
                                popUpTo("main") { inclusive = true }
                            }

                        },
                        onFailure = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }) {
                    Text("탈퇴하기", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CommonTopBar(onLeftActionClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.charac_main_nontext),
                contentDescription = "디아비서",
                modifier = Modifier.size(96.dp)
            )

            Text(
                text = "디아비서",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                color = DiaViseoColors.Basic
            )

            Text(
                text = "내 정보",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp),
                color = DiaViseoColors.Basic
            )

            ProfileInfoRow(title = "이름", value = profile?.name ?: "")
            ProfileInfoRow(title = "성별", value = if (profile?.gender == "M") "남성" else "여성")
            ProfileInfoRow(title = "핸드폰 번호", value = profile?.phone ?: "")
            ProfileInfoRow(title = "닉네임", value = profile?.nickname ?: "", onClick = { showNicknameSheet = true })
            ProfileInfoRow(
                title = "생년월일",
                value = profile?.birthday?.replace("-", "년 ")?.replace("-", "월 ")?.plus("일") ?: "",
                onClick = { showBirthDateSheet = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "회원탈퇴",
                    fontSize = 14.sp,
                    color = DiaViseoColors.Unimportant,
                    modifier = Modifier.clickable {
                        showDialog = true  // 다이얼로그 열기
                    }
                )
                Text(
                    text = "로그아웃",
                    fontSize = 14.sp,
                    color = DiaViseoColors.Unimportant,
                    modifier = Modifier.clickable {
                        authViewModel.logout()
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileInfoRow(title: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp, color = DiaViseoColors.Unimportant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = if (onClick != null) FontWeight.Medium else FontWeight.Normal,
                color = DiaViseoColors.Basic
            )
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "수정",
                    tint = DiaViseoColors.Unimportant
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun UserProfileEditScreenPreview() {
//    val navController = rememberNavController()
//    UserProfileEditScreen(navController = navController)
//}
