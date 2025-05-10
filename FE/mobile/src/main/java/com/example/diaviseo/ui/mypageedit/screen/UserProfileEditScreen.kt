package com.example.diaviseo.ui.mypageedit.screen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.mypageedit.bottomsheet.EditBirthDateBottomSheet
import com.example.diaviseo.ui.mypageedit.bottomsheet.EditNicknameBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileEditScreen(navController: NavHostController) {
    val nicknameSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val birthDateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showNicknameSheet by remember { mutableStateOf(false) }
    var showBirthDateSheet by remember { mutableStateOf(false) }

    // 닉네임 BottomSheet
    if (showNicknameSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNicknameSheet = false },
            sheetState = nicknameSheetState
        ) {
            EditNicknameBottomSheet(
                initialNickname = "디아25",
                onSave = {
                    showNicknameSheet = false
                },
                onDismiss = { showNicknameSheet = false }
            )
        }
    }

    // 생년월일 BottomSheet
    if (showBirthDateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBirthDateSheet = false },
            sheetState = birthDateSheetState
        ) {
            EditBirthDateBottomSheet(
                initialBirthDate = "2000.02.14",
                onSave = {
                    showBirthDateSheet = false
                },
                onDismiss = { showBirthDateSheet = false }
            )
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                onLeftActionClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.charac_main_nontext),
                contentDescription = "다이아비서",
                modifier = Modifier.size(96.dp)
            )

            Text(
                text = "다이비서",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Text(
                text = "내 정보",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            ProfileInfoRow(title = "이름", value = "김디아")
            ProfileInfoRow(title = "성별", value = "여성")
            ProfileInfoRow(title = "핸드폰 번호", value = "010-1234-5678")
            ProfileInfoRow(title = "닉네임", value = "디아25", onClick = { showNicknameSheet = true })
            ProfileInfoRow(title = "생년월일", value = "2000년 2월 14일", onClick = { showBirthDateSheet = true })

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "회원탈퇴",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { /* TODO */ }
                )
                Text(
                    text = "로그아웃",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { /* TODO */ }
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
        Text(text = title, fontSize = 14.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 14.sp)
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "수정",
                    tint = Color.Gray
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun UserProfileEditScreenPreview() {
    val navController = rememberNavController()
    UserProfileEditScreen(navController = navController)
}

