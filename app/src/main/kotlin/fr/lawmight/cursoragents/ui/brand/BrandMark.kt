package fr.lawmight.cursoragents.ui.brand

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

/**
 * Branded "c" mark — a 270-degree arc with rounded caps centered in a tinted
 * tile. Use [BrandMark] for hero placements (onboarding) and [BrandWordmark]
 * for app-bar headers.
 */
@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    background: Color = MaterialTheme.colorScheme.primary,
    foreground: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.large)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val px = this.size.minDimension
            val stroke = px * 0.13f
            val inset = px * 0.22f
            val arcSize = Size(px - inset * 2, px - inset * 2)
            val topLeft = Offset(inset, inset)
            // 270-degree arc, gap on the right (3 o'clock).
            drawArc(
                color = foreground,
                startAngle = 30f,
                sweepAngle = 300f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
    }
}

@Composable
fun BrandWordmark(
    modifier: Modifier = Modifier,
    text: String = "Cursor Agents",
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        BrandMark(size = 28.dp)
        Spacer(Modifier.width(spacing.xs))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun BrandMarkPreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BrandMark(size = 96.dp)
            BrandWordmark()
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun BrandMarkPreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BrandMark(size = 96.dp)
            BrandWordmark()
        }
    }
}
