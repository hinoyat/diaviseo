package com.example.diaviseo.ui.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.ui.theme.medium14
import com.example.diaviseo.ui.theme.regular14
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager


@Composable
fun CommonSearchTopBar(
    placeholder: String,
    navController: NavController,
    keyword: String,                         // 현재 입력 중인 값
    onKeywordChange: (String) -> Unit,        // 입력 변경 시 호출할 함수
    onFocusChanged: ((Boolean) -> Unit)? = null, // 검색창 클릭시 검색 결과창 띄우기 위함 (null도 허용)
    onCancelClick: (() -> Unit)? = null  // 취소 로직 위임
) {
    val focusManager = LocalFocusManager.current


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            )
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 검색 입력 필드
        TextField(
            value = keyword,
            onValueChange = onKeywordChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = regular14,
                    color = Color.Gray
                )
            },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .onFocusChanged { focusState ->
                    onFocusChanged?.invoke(focusState.isFocused) // ✅ null 체크 후 호출
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                disabledContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색",
                    tint = Color.Gray
                )
            },
            shape = RoundedCornerShape(15.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 취소 버튼
        Text(
            text = "취소",
            style = medium14,
            color = Color.Black,
            modifier = Modifier.clickable {
                focusManager.clearFocus() // 키보드 닫기
                onCancelClick?.invoke() ?: navController.popBackStack() // null일 땐 기본 뒤로가기
            }
        )
    }
}

