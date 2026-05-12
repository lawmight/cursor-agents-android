package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.m, vertical = spacing.xs),
    )
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun SectionHeaderPreviewLight() {
    PreviewSurface(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            SectionHeader(text = "Repository")
            SectionHeader(text = "Prompt")
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun SectionHeaderPreviewDark() {
    PreviewSurface(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            SectionHeader(text = "Repository")
            SectionHeader(text = "Prompt")
        }
    }
}
