package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun RepoChip(
    owner: String,
    name: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
    val spacing = LocalSpacing.current
    val rowMod =
        modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = spacing.s, vertical = spacing.xs)
    Row(
        modifier = rowMod,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(spacing.xs))
        Text(
            text = "$owner/",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (onClose != null) {
            Spacer(Modifier.width(spacing.xs))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable(onClick = onClose),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun RepoChipPreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RepoChip(owner = "lawmight", name = "cursor-agents-android")
            RepoChip(owner = "lawmight", name = "cursor-agents-android", onClose = {})
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun RepoChipPreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RepoChip(owner = "lawmight", name = "cursor-agents-android")
            RepoChip(owner = "lawmight", name = "cursor-agents-android", onClose = {})
        }
    }
}
