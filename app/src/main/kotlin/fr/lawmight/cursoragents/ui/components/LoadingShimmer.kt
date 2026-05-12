package fr.lawmight.cursoragents.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

/**
 * Animated shimmer block. Width is driven by the modifier (typically
 * fillMaxWidth or a fixed width) and the height defaults to 16dp.
 */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1200),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer-progress",
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surface
    val shimmerWidth = 240f
    val translate = (-shimmerWidth) + (progress * (shimmerWidth * 2))
    val brush =
        Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(translate, 0f),
            end = Offset(translate + shimmerWidth, 0f),
        )
    Box(
        modifier =
            modifier
                .height(height)
                .clip(MaterialTheme.shapes.small)
                .background(brush),
    )
}

/**
 * Shimmer skeleton for AgentCard rows in the agent list.
 */
@Composable
fun AgentCardShimmer(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .padding(spacing.m),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(modifier = Modifier.width(60.dp), height = 18.dp)
            Spacer(Modifier.width(spacing.xs))
            ShimmerBlock(modifier = Modifier.width(120.dp), height = 14.dp)
        }
        Spacer(Modifier.height(spacing.s))
        ShimmerBlock(modifier = Modifier.fillMaxWidth(), height = 14.dp)
        Spacer(Modifier.height(spacing.xs))
        ShimmerBlock(modifier = Modifier.fillMaxWidth(0.7f), height = 14.dp)
    }
}

@Composable
fun ListItemShimmer(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.m, vertical = spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Spacer(Modifier.width(spacing.s))
        Column(modifier = Modifier.fillMaxWidth()) {
            ShimmerBlock(modifier = Modifier.width(140.dp), height = 14.dp)
            Spacer(Modifier.height(spacing.xxs))
            ShimmerBlock(modifier = Modifier.width(80.dp), height = 12.dp)
        }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun LoadingShimmerPreviewLight() {
    PreviewSurface(darkTheme = false) {
        Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            AgentCardShimmer()
            ListItemShimmer()
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun LoadingShimmerPreviewDark() {
    PreviewSurface(darkTheme = true) {
        Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            AgentCardShimmer()
            ListItemShimmer()
        }
    }
}
