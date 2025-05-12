package com.example.diaviseo.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diaviseo.R
import com.example.diaviseo.ui.components.BottomButtonSection
import com.example.diaviseo.ui.components.onboarding.StepProgressBar
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.viewmodel.AuthViewModel
import com.example.diaviseo.viewmodel.goal.GoalViewModel
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
@Composable
fun GoalSelectScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    goalViewModel: GoalViewModel
) {
    val nickname by authViewModel.nickname.collectAsState()
    val height by authViewModel.height.collectAsState()
    val weight by authViewModel.weight.collectAsState()
    val selectedGoal by authViewModel.goal.collectAsState()
//    val selectedGoal by goalViewModel.goal.collectAsState()

    val bmi = remember(height, weight) {
        val h = height.toFloatOrNull()?.div(100f)
        val w = weight.toFloatOrNull()
        if (h != null && w != null && h > 0) (w / (h * h)) else 0f
    }

    val statusText = when {
        bmi == 0f -> "BMI 계산 불가"
        bmi < 18.5 -> "저체중"
        bmi < 23 -> "정상"
        bmi < 25 -> "과체중"
        else -> "비만"
    }

    val recommendedGoal = when {
        bmi < 18.5 -> "체중 증량"
        bmi < 23 -> "체중 유지"
        else -> "체중 감량"
    }
    val bmiColor = when {
        bmi < 18.5 -> Color(0xFF70A1FF) // 저체중 - 파랑
        bmi < 23 -> Color(0xFF2ED573)   // 정상 - 초록
        bmi < 25 -> Color(0xFFFFC107)   // 과체중 - 주황
        else -> Color(0xFFFF6B81)       // 비만 - 빨강
    }
    var tempGoal by remember { mutableStateOf(selectedGoal) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.gradient_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues()
                )
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))

                StepProgressBar(currentStep = 3)

                Spacer(modifier = Modifier.height(32.dp))


                Text(
                    text = buildAnnotatedString {
                        append("$nickname 님의 BMI는 ${"%.1f".format(bmi)}로\n현재 ")
                        withStyle(
                            style = SpanStyle(
                                color = bmiColor,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(statusText)
                        }
                        append(" 상태예요.")
                    },
                    fontSize = 20.sp,
                    style = LocalTextStyle.current.copy(color = Color.Black) // 💡 중요
                )


                Spacer(modifier = Modifier.height(32.dp))

                Text("관리 목표를 선택해주시면\n맞춤형 안내를 드릴게요.")

                Spacer(modifier = Modifier.height(24.dp))

                GoalOptionButton("체중 감량", tempGoal == "WEIGHT_LOSS", isRecommended = recommendedGoal == "체중 감량") { tempGoal = "WEIGHT_LOSS" }
                GoalOptionButton("체중 유지", tempGoal == "WEIGHT_MAINTENANCE", isRecommended = recommendedGoal == "체중 유지") { tempGoal = "WEIGHT_MAINTENANCE" }
                GoalOptionButton("체중 증량", tempGoal == "WEIGHT_GAIN", isRecommended = recommendedGoal == "체중 증량") { tempGoal = "WEIGHT_GAIN" }
            }

            BottomButtonSection(
                text = "목표 설정하기",
                enabled = tempGoal.isNotBlank(),
                onClick = {
//                    goalViewModel.setGoal(tempGoal)
                    authViewModel.setGoal(tempGoal)
                    navController.navigate("onboarding/finalguide")
                    authViewModel.signUpWithDia(tempGoal)
                }
            )
        }
    }
}

@Composable
fun GoalOptionButton(text: String, selected: Boolean, isRecommended: Boolean = false,  onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) DiaViseoColors.Main1 else Color(0xFFE0E0E0),
            contentColor = if (selected) Color.White else Color.Black
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(text)
            if (isRecommended) {
                Text(
                    text = "추천",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiaViseoColors.Main1,
                    modifier = Modifier
                        .background(Color(0xFFE0F0FF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }

}
