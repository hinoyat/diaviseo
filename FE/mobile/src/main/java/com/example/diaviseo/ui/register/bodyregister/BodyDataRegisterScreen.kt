package com.example.diaviseo.ui.register.bodyregister

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.CommonTopBar
import com.example.diaviseo.ui.theme.*
import com.example.diaviseo.ui.register.bodyregister.components.LabeledNumberInputField
import com.example.diaviseo.ui.register.bodyregister.components.SelectableIconCard
import com.example.diaviseo.viewmodel.register.body.BodyRegisterViewModel

enum class BodyRegisterType {
    PHOTO, MANUAL
}

@Composable
fun BodyDataRegisterScreen(
    navController: NavController,
    viewModel: BodyRegisterViewModel = viewModel()
) {
    val context = LocalContext.current

    // 체성분 등록 상태값
    val selectedOption = remember { mutableStateOf<BodyRegisterType?>(null) }

    val weight by viewModel.weight.collectAsState()
    val fat by viewModel.bodyFat.collectAsState()
    val muscle by viewModel.muscleMass.collectAsState()


    // 모든 입력 필드가 채워져 있어야 등록 가능
    val isManualInputValid = viewModel.isInputValid()
    val isFormValid = when (selectedOption.value) {
        BodyRegisterType.MANUAL -> isManualInputValid
        BodyRegisterType.PHOTO -> true
        else -> false
    }

    val bodyInputs = listOf(
        Triple("체중", weight) { value: String -> viewModel.onWeightChange(value) },
        Triple("체지방량", fat) { value: String -> viewModel.onBodyFatChange(value) },
        Triple("골격근량", muscle) { value: String -> viewModel.onMuscleMassChange(value) }
    )

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

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            SelectableIconCard(
                label = "사진으로 등록",
                iconResId = R.drawable.camera,
                isSelected = selectedOption.value == BodyRegisterType.PHOTO,
                onClick = { selectedOption.value = BodyRegisterType.PHOTO },
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
            )

            SelectableIconCard(
                label = "직접 입력하기",
                iconResId = R.drawable.pencil,
                isSelected = selectedOption.value == BodyRegisterType.MANUAL,
                onClick = { selectedOption.value = BodyRegisterType.MANUAL },
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
            )
        }
        // 직접 입력 선택 시 입력 필드 노출
        if (selectedOption.value == BodyRegisterType.MANUAL) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                bodyInputs.forEach { (label, value, onChange) ->
                    LabeledNumberInputField(
                        label = label,
                        unit = "kg",
                        value = value,
                        onValueChange = onChange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 하단 버튼
        BottomButtonSection(
            text = "등록",
            enabled = isFormValid,
            onClick = {
                viewModel.registerBodyData(
                    onSuccess = {
                        viewModel.resetInput() // 입력값 초기화
                        Toast.makeText(context, "체성분 등록 완료!", Toast.LENGTH_SHORT).show()
                    },
                    onError = { message ->
                        Toast.makeText(context, message,Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}


