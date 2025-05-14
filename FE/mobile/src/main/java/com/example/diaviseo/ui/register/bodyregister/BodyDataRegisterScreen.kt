package com.example.diaviseo.ui.register.bodyregister

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.diaviseo.ui.register.bodyregister.components.LabeledDateInputField
import com.example.diaviseo.ui.register.bodyregister.components.LabeledNumberInputField
import com.example.diaviseo.ui.register.bodyregister.components.SelectableIconCard
import com.example.diaviseo.viewmodel.register.body.BodyRegisterViewModel
import com.example.diaviseo.ui.theme.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

enum class BodyRegisterType {
    PHOTO, MANUAL
}

@Composable
fun BodyDataRegisterScreen(
    navController: NavController,
    viewModel: BodyRegisterViewModel = viewModel()
) {
    val selectedOption = remember { mutableStateOf<BodyRegisterType?>(null) }

    val weight by viewModel.weight.collectAsState()
    val fat by viewModel.bodyFat.collectAsState()
    val muscle by viewModel.muscleMass.collectAsState()
    val height by viewModel.height.collectAsState()
    val year by viewModel.year.collectAsState()
    val month by viewModel.month.collectAsState()
    val day by viewModel.day.collectAsState()

    val isManualInputValid = viewModel.isInputValid()
    val isFormValid = when (selectedOption.value) {
        BodyRegisterType.MANUAL -> isManualInputValid
        BodyRegisterType.PHOTO -> true
        else -> false
    }

    val bodyInputs = listOf(
        Triple("키", height, viewModel::onHeightChange),
        Triple("체중", weight, viewModel::onWeightChange),
        Triple("체지방량", fat, viewModel::onBodyFatChange),
        Triple("골격근량", muscle, viewModel::onMuscleMassChange)
    )

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri.value = uri

            // uri -> MultipartBody.Part 변환
            val stream = context.contentResolver.openInputStream(uri)
            stream?.let {
                val bytes = it.readBytes()
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = "body_photo.jpg",
                    body = requestBody
                )

                // OCR API 호출
                viewModel.sendOcrRequest(part)
            }
        }
    }
    val isOcrLoading by viewModel.isOcrLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        CommonTopBar(
            onRightActionClick = { navController.popBackStack() }
        )

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
                onClick = {
                    selectedOption.value = BodyRegisterType.PHOTO
                    galleryLauncher.launch("image/*") // 갤러리 호출
                          },
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

        if (selectedOption.value != null) {
            Spacer(modifier = Modifier.height(32.dp))

            if (isOcrLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WaveTextSimple("업로드한 사진에서 정보를 추출하고 있어요!")
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    bodyInputs.forEach { (label, value, onChange) ->
                        LabeledNumberInputField(
                            label = label,
                            unit = if (label == "키") "cm" else "kg",
                            value = value,
                            onValueChange = onChange,
                            modifier = Modifier.width(130.dp)
                        )
                    }

                    LabeledDateInputField(
                        year = year,
                        month = month,
                        day = day,
                        onYearChange = viewModel::onYearChange,
                        onMonthChange = viewModel::onMonthChange,
                        onDayChange = viewModel::onDayChange
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        BottomButtonSection(
            text = "등록",
            enabled = isFormValid,
            onClick = {
                viewModel.registerBodyData(
                    onSuccess = {
                        viewModel.resetInput()
                        Toast.makeText(context, "체성분 등록 완료!", Toast.LENGTH_SHORT).show()
                    },
                    onError = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}


@Composable
fun WaveTextSimple(
    text: String,
    waveHeight: Float = -4f,
    waveSpeed: Int = 1000,
    waveDelayPerChar: Int = 100,
    alphaMin: Float = 0.4f,
    alphaMax: Float = 1f,
) {
    val transition = rememberInfiniteTransition(label = "wave")

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        text.forEachIndexed { index, char ->
            val offsetY by transition.animateFloat(
                initialValue = 0f,
                targetValue = waveHeight,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = waveSpeed,
                        delayMillis = index * waveDelayPerChar,
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "offset-$index"
            )

            val alpha by transition.animateFloat(
                initialValue = alphaMin,
                targetValue = alphaMax,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = waveSpeed,
                        delayMillis = index * waveDelayPerChar,
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha-$index"
            )

            Text(
                text = char.toString(),
                style = regular16,
                color = DiaViseoColors.Placeholder.copy(alpha = alpha),
                modifier = Modifier.offset(y = offsetY.dp)
            )
        }
    }
}
