package com.example.diaviseo.ui.register.bodyregister

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.theme.*

enum class BodyRegisterType {
    PHOTO, MANUAL
}

@Composable
fun BodyDataRegisterScreen(navController: NavController) {
    // 선택된 등록 방식과 사용자 입력 상태값 관리
    var selectedOption by remember { mutableStateOf<BodyRegisterType?>(null) }
    var weight by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("") }

    // 모든 입력 필드가 채워져 있어야 등록 가능
    val isManualInputValid = weight.isNotBlank() && fat.isNotBlank() && muscle.isNotBlank()
    val isFormValid = when (selectedOption) {
        BodyRegisterType.MANUAL -> isManualInputValid
        BodyRegisterType.PHOTO -> true // TODO: 사진 인식 여부로 변경 예정
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // 상단 X 버튼 있는 공통 AppBar
        CommonTopBar(
            onRightActionClick = { navController.popBackStack() }
        )

//        Spacer(modifier = Modifier.height(22.dp))
        // 타이틀 및 설명 텍스트
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "체성분 데이터를\n등록해주세요",
                style = bold24,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "인바디 결과 사진으로 자동 인식하거나\n직접 입력할 수 있어요!",
                style = medium14,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 등록 방식 선택 카드 2개 (가로 정렬)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionCard(
                label = "사진으로 등록",
                iconResId = R.drawable.camera,
                isSelected = selectedOption == BodyRegisterType.PHOTO,
                onClick = { selectedOption = BodyRegisterType.PHOTO },
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
            )

            OptionCard(
                label = "직접 입력하기",
                iconResId = R.drawable.pencil,
                isSelected = selectedOption == BodyRegisterType.MANUAL,
                onClick = { selectedOption = BodyRegisterType.MANUAL },
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
            )
        }
        // 직접 입력 선택 시 입력 필드 노출
        if (selectedOption == BodyRegisterType.MANUAL) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LabeledInputField(label = "체중", unit = "kg", value = weight) { weight = it }
                LabeledInputField(label = "체지방량", unit = "kg", value = fat) { fat = it }
                LabeledInputField(label = "골격근량", unit = "kg", value = muscle) { muscle = it }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 하단 버튼
        BottomButtonSection(
            text = "등록",
            enabled = isFormValid,
            onClick = {
                // TODO: 등록 처리
            }
        )
    }
}

// 사진 등록 / 직접 입력 카드 UI 정의
@Composable
fun OptionCard(
    label: String,
    iconResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF516AF0) else Color(0xFFC5C5C5)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                modifier = Modifier.size(90.dp)
            )
            Text(
                text = label,
                style = semibold16,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LabeledInputField(label: String, unit: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        // 왼쪽 텍스트 (체중, 체지방량 등)
        Text(
            text = label,
            style = bold16,
            modifier = Modifier
                .weight(1f)
        )
        // 오른쪽 입력 필드
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(130.dp)
                .height(48.dp),
            textStyle = medium14,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color(0xFF516AF0),
                unfocusedBorderColor = Color(0xFFCCCCCC),
                disabledBorderColor = Color(0xFFEEEEEE),
                cursorColor = Color(0xFF516AF0)
            ),
            trailingIcon = {
                Text(
                    text = unit,
                    style = medium14,
                    color = if (value.isBlank()) Color.Gray else Color.Black
                )
            }
        )
    }
}
