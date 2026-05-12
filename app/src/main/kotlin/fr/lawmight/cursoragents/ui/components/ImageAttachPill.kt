package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun ImageAttachPill(
    fileName: String,
    modifier: Modifier = Modifier,
    imageUri: Any? = null,
    onRemove: (() -> Unit)? = null,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(end = spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.width(spacing.xs))
        Text(
            text = fileName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (onRemove != null) {
            Spacer(Modifier.width(spacing.xs))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable(onClick = onRemove),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun ImageAttachPillPreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ImageAttachPill(fileName = "screenshot.png", onRemove = {})
            ImageAttachPill(fileName = "design-mockup.jpg")
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun ImageAttachPillPreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ImageAttachPill(fileName = "screenshot.png", onRemove = {})
            ImageAttachPill(fileName = "design-mockup.jpg")
        }
    }
}
