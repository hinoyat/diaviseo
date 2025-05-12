package com.example.diaviseo.ui.main.components.goal.meal

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.Wanted
import com.example.diaviseo.ui.theme.bold12
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DonutChartWithLegend(
    modifier: Modifier = Modifier,
    calories: Int,
    calorieGoal: Int,
    carbRatio: Float,     // 0.0 ~ 1.0
    sugarRatio: Float,
    proteinRatio: Float,
    fatRatio: Float
) {
    val ratios = listOf(carbRatio, sugarRatio, proteinRatio, fatRatio)
    val colors = listOf(
        DiaViseoColors.Carbohydrate,
        DiaViseoColors.Sugar,
        DiaViseoColors.Protein,
        DiaViseoColors.Fat
    )

    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(120.dp)
            ) {
                val thicknessPx = 32.dp.toPx()
                val radius = size.minDimension / 2f
                // 배경 도넛 (빈 영역)
                drawArc(
                    color = Color(0xFFF2F2F2),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = thicknessPx)
                )

                // 텍스트용 Paint 준비
                val textStyle = bold12                // theme 에 정의된 TextStyle
                val textSizePx = with(density) { textStyle.fontSize.toPx() }
                val textPaint = Paint().apply {
                    isAntiAlias = true
                    color = Color.White.toArgb()
                    textAlign = Paint.Align.CENTER
                    textSize = textSizePx

                }
                val fm = textPaint.fontMetrics

                // 실제 세그먼트 + 텍스트
                var startAngle = -90f
                ratios.forEachIndexed { index, ratio ->
                    val sweep = 360f * ratio
                    // 세그먼트 그리기
                    drawArc(
                        color = colors[index],
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = thicknessPx)
                    )

                    // 텍스트 그리기 (두께 중간 지점)
                    val midAngle = startAngle + sweep / 2f
                    val rad = Math.toRadians(midAngle.toDouble())
                    val textRadius = radius - thicknessPx / 10f

                    val x = center.x + (textRadius * cos(rad)).toFloat()
                    val yBase = center.y + (textRadius * sin(rad)).toFloat()
                    // fm.descent + fm.ascent 는 baseline↔center offset
                    val y = yBase - (fm.top + fm.bottom) / 2f

                    drawContext.canvas.nativeCanvas.drawText(
                        "${(ratio * 100).toInt()}%",
                        x,
                        y,
                        textPaint
                    )
                    startAngle += sweep
                }
            }
        }
        Spacer(modifier = Modifier.width(30.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = buildAnnotatedString {
                    // 1) 큰 숫자 (섭취 칼로리)
                    withStyle(style = SpanStyle(
                        fontFamily = Wanted,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiaViseoColors.Basic
                    )) {
                        append("$calories")
                    }
                    // 2) 구분자 + 목표/단위는 작게
                    append("  ")
                    withStyle(style = SpanStyle(
                        fontFamily = Wanted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = DiaViseoColors.Basic
                    )) {
                        append("/ $calorieGoal kcal")
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LegendRow("탄수화물", DiaViseoColors.Carbohydrate)
            LegendRow("당류", DiaViseoColors.Sugar)
            LegendRow("단백질", DiaViseoColors.Protein)
            LegendRow("지방", DiaViseoColors.Fat)
        }
    }
}

@Composable
private fun LegendRow(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = DiaViseoColors.Basic
        )
    }
}