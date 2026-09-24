package com.peterwachira.cashipay.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/** Displays the CashiPay wordmark with a small code-drawn brand symbol. */
@Composable
internal fun CashiPayWordmark(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .size(28.dp)
                .clearAndSetSemantics {}
        ) {
            drawCircle(
                color = contentColor.copy(alpha = 0.16f),
                radius = size.minDimension / 2f
            )
            drawArc(
                color = contentColor,
                startAngle = 35f,
                sweepAngle = 250f,
                useCenter = false,
                topLeft = Offset(size.width * 0.22f, size.height * 0.22f),
                size = Size(size.width * 0.56f, size.height * 0.56f),
                style = Stroke(width = size.width * 0.12f, cap = StrokeCap.Round)
            )
            drawCircle(
                color = contentColor,
                radius = size.width * 0.08f,
                center = Offset(size.width * 0.70f, size.height * 0.30f)
            )
        }

        Row {
            Text(
                text = "Cashi",
                color = contentColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Pay",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

/** Draws the soft geometric decoration used on prominent CashiPay surfaces. */
@Composable
internal fun CashiHeroDecoration(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Canvas(
        modifier = modifier.clearAndSetSemantics {}
    ) {
        drawCircle(
            color = color.copy(alpha = 0.08f),
            radius = size.width * 0.42f,
            center = Offset(size.width * 0.88f, size.height * 0.18f)
        )
        drawCircle(
            color = color.copy(alpha = 0.06f),
            radius = size.width * 0.32f,
            center = Offset(size.width * 0.12f, size.height * 1.02f)
        )
        drawArc(
            color = color.copy(alpha = 0.20f),
            startAngle = 155f,
            sweepAngle = 165f,
            useCenter = false,
            topLeft = Offset(size.width * 0.62f, -size.height * 0.12f),
            size = Size(size.width * 0.46f, size.width * 0.46f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

/** Displays the scalable confirmation illustration used after a successful payment. */
@Composable
internal fun PaymentSuccessGraphic(
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer

    Box(
        modifier = modifier.clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val rayStart = size.minDimension * 0.39f
            val rayEnd = size.minDimension * 0.47f

            repeat(12) { index ->
                val angle = Math.toRadians((index * 30.0) - 90.0)
                val start = Offset(
                    x = center.x + cos(angle).toFloat() * rayStart,
                    y = center.y + sin(angle).toFloat() * rayStart
                )
                val end = Offset(
                    x = center.x + cos(angle).toFloat() * rayEnd,
                    y = center.y + sin(angle).toFloat() * rayEnd
                )
                drawLine(
                    color = primary.copy(alpha = 0.34f),
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = container.copy(alpha = 0.55f),
                radius = size.minDimension * 0.34f,
                center = center
            )
            drawCircle(
                color = primary,
                radius = size.minDimension * 0.23f,
                center = center
            )

            val check = Path().apply {
                moveTo(size.width * 0.39f, size.height * 0.51f)
                lineTo(size.width * 0.47f, size.height * 0.59f)
                lineTo(size.width * 0.63f, size.height * 0.42f)
            }
            drawPath(
                path = check,
                color = Color.White,
                style = Stroke(
                    width = size.minDimension * 0.045f,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}
