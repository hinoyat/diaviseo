package com.example.diaviseo.ui.detail.components.meal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.diaviseo.R
import com.example.diaviseo.network.meal.dto.res.MealFoodResponse
import com.example.diaviseo.network.meal.dto.res.MealNutritionResponse
import com.example.diaviseo.ui.components.onboarding.MainButton
import com.example.diaviseo.ui.theme.*
import java.math.BigDecimal
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MealCard(
    title: String,                // 예: 아침, 점심, 저녁, 간식
    kcal: Int,
    eatingTime: String,          // 예: "08:30:00"
    nutrition: MealNutritionResponse,  // "탄수화물" to 56.9, ...
    foods: List<MealFoodResponse>,
    gradient: Brush,
    mealIconRes: Int,
    onEditClick: () -> Unit,
    imgUrl: String? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    val showPreview = remember { mutableStateOf(false) }

//    val mealIcon = when (mealIconRes) {
//        "BREAKFAST" -> R.drawable.morning
//        "LUNCH" -> R.drawable.lunch
//        "DINNER" -> R.drawable.night
//        else -> R.drawable.apple
//    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x1A222222),
                spotColor = Color(0x1A222222)
            )
            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // 상단: 아이콘, 타이틀, 시간, kcal
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = mealIconRes),
                contentDescription = "식사 아이콘",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$title ",
                style = bold16,
                color = DiaViseoColors.Basic
            )
            Text(
                text = "(${kcal} kcal)",
                style = bold16,
                color = DiaViseoColors.Main1
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatAmPm(eatingTime),
                style = regular14,
                color = DiaViseoColors.Basic
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 영양 성분
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                NutritionRow(
                    label = "탄수화물",
                    value = nutrition.totalCarbohydrate,
                    color = DiaViseoColors.Carbohydrate,
                    modifier = Modifier.weight(1f)
                )
                NutritionRow(
                    label = "단백질",
                    value = nutrition.totalProtein,
                    color = DiaViseoColors.Protein,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                NutritionRow(
                    label = "지방",
                    value = nutrition.totalFat,
                    color = DiaViseoColors.Fat,
                    modifier = Modifier.weight(1f)
                )
                NutritionRow(
                    label = "당류",
                    value = nutrition.totalSugar,
                    color = DiaViseoColors.Sugar,
                    modifier = Modifier.weight(1f)
                )
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        // 음식 리스트
        if (!isExpanded) {
            foods.forEach {
                MealFoodRow(
                    name = it.foodName,
                    quantityLabel = "${it.quantity} 인분",
                    kcal = it.totalCalorie
                )
            }
        } else {
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    foods.forEach {
                        Spacer(modifier = Modifier.height(8.dp))
                        MealFoodRow(
                            name = it.foodName,
                            quantityLabel = "${it.quantity} 인분",
                            kcal = it.totalCalorie
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MacroBreakdownRow(it)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!imgUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "식단 사진 📷",
                            style = bold16,
                            color = DiaViseoColors.Basic
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        AsyncImage(
                            model = imgUrl,
                            contentDescription = "식단 이미지",
                            modifier = Modifier
                                .width(284.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showPreview.value = true }
                                .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    MainButton(
                        text = "수정하기",
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "펼치기/접기"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isExpanded) "접기" else "더보기",
                style = regular14,
                color = DiaViseoColors.Basic
            )
        }
    }
}

@Composable
fun NutritionRow(
    label: String,
    value: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label ${"%.1f".format(value)}g",
            style = regular14,
            color = DiaViseoColors.Basic
        )
    }
}

@Composable
fun MacroBreakdownRow(food: MealFoodResponse) {
    val macros = listOf(
        Triple("탄", food.totalCarbohydrate, DiaViseoColors.Carbohydrate),
        Triple("단", food.totalProtein, DiaViseoColors.Protein),
        Triple("지", food.totalFat, DiaViseoColors.Fat),
        Triple("당", food.totalSugar, DiaViseoColors.Sugar)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        macros.forEach { (label, value, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = bold14,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${"%.1f".format(value)}g",
                    style = regular14,
                    color = DiaViseoColors.Basic
                )
            }
        }
    }
}

fun formatAmPm(time: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val parsed = LocalTime.parse(time, formatter)
        val hour = if (parsed.hour < 12) "AM" else "PM"
        "$hour ${parsed.hour % 12}:${parsed.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        ""
    }
}