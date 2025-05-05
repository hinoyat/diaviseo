package com.example.diaviseo.ui.main.components.goal

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaviseo.ui.theme.DiaViseoColors
import kotlin.math.ceil

/**
 * StakedBarChart: 단일 Canvas 에서 Y축 자동 레이블과 Grid, Bar, X축 레이블을 그리고,
 * 바 클릭 시 onBarClick 콜백 호출
 */
@Composable
fun StakedBarChart(
    data: List<StakedBarChartData.Entry>,
    modifier: Modifier = Modifier,
    barWidth: Dp = 24.dp,
    maxBarHeight: Dp = 140.dp,
    ySteps: Int = 5,
    onBarClick: ((StakedBarChartData.Entry) -> Unit)? = null
) {
    if (data.isEmpty()) return

    // Y축 최대값 & 스텝 계산
    val maxY = data.maxOf { it.total }
    val stepSize = ceil(maxY.toFloat() / ySteps).toInt()
    val yAxisValues = (0..ySteps).map { it * stepSize }

    // Density for Dp to Px
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .height(maxBarHeight + 40.dp) // 차트 높이 + X축 레이블 여유
            .pointerInput(data) {
                detectTapGestures { tapOffset ->
                    // 터치된 X 위치에 해당하는 Entry 찾기
                    val widthPx = size.width
                    val heightPx = size.height - with(density) { 24.dp.toPx() }
                    val labelArea = with(density) { 40.dp.toPx() }
                    val slotWidth = (widthPx - labelArea) / data.size
                    val bw = with(density) { barWidth.toPx() }

                    data.forEachIndexed { idx, entry ->
                        val startX = labelArea + idx * slotWidth + (slotWidth - bw) / 2f
                        if (tapOffset.x in startX..(startX + bw) &&
                            tapOffset.y in 0f..heightPx
                        ) {
                            onBarClick?.invoke(entry)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height - with(density) { 24.dp.toPx() }
            val labelArea = with(density) { 24.dp.toPx() }

            // Paint for nativeCanvas
            val textPaint = Paint().apply {
                isAntiAlias = true
                color = DiaViseoColors.Basic.toArgb()
                textSize = with(density) { 12.sp.toPx() }
                textAlign = Paint.Align.CENTER
            }

            // 1) Y축 Grid & Label
            yAxisValues.forEach { yv ->
                val yPos = h - (yv / (stepSize * ySteps.toFloat())) * h
                // Grid line
                drawLine(
                    color = Color.LightGray,
                    start = Offset(labelArea, yPos),
                    end = Offset(w, yPos),
                    strokeWidth = with(density) { 1.dp.toPx() }
                )
                // Label
                drawContext.canvas.nativeCanvas.drawText(
                    "${yv}",
                    with(density) { 4.dp.toPx() },
                    yPos + with(density) { 4.dp.toPx() },
                    textPaint
                )
            }

            // 2) Bars & X축 레이블
            val slotWidth = (w - labelArea) / data.size
            data.forEachIndexed { idx, entry ->
                val bwPx = with(density) { barWidth.toPx() }
                val startX = labelArea + idx * slotWidth + (slotWidth - bwPx) / 2f
                var currentY = h

                // 각 세그먼트 누적해서 그리기
                listOf(
                    DiaViseoColors.Carbohydrate to entry.carbs,
                    DiaViseoColors.Protein to entry.protein,
                    DiaViseoColors.Fat to entry.fat,
                    DiaViseoColors.Sugar to entry.sugar
                ).forEach { (color, value) ->
                    val segmentHeight = (value / (stepSize * ySteps.toFloat())) * h
                    currentY -= segmentHeight
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(startX, currentY),
                        size = Size(bwPx, segmentHeight),
                    )
                }

                // X축 레이블 (아래 여유 24dp)
                drawContext.canvas.nativeCanvas.drawText(
                    entry.label,
                    startX + bwPx / 2f,
                    h + with(density) { 16.dp.toPx() },
                    textPaint
                )
            }
        }
    }
}