package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class GhostIconButtonVariant { Ghost, Tonal }

@Composable
fun GhostIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: GhostIconButtonVariant = GhostIconButtonVariant.Ghost,
) {
    val sizeMod = modifier.size(40.dp)
    when (variant) {
        GhostIconButtonVariant.Ghost ->
            IconButton(
                onClick = onClick,
                enabled = enabled,
                modifier = sizeMod,
                colors =
                    IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(20.dp))
            }
        GhostIconButtonVariant.Tonal ->
            FilledTonalIconButton(
                onClick = onClick,
                enabled = enabled,
                modifier = sizeMod,
                shape = MaterialTheme.shapes.small,
                colors =
                    IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(20.dp))
            }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun GhostIconButtonPreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GhostIconButton(Icons.Default.Settings, onClick = {}, contentDescription = "Settings")
            GhostIconButton(Icons.Default.MoreVert, onClick = {}, contentDescription = "More")
            GhostIconButton(
                icon = Icons.Default.Close,
                onClick = {},
                contentDescription = "Close",
                variant = GhostIconButtonVariant.Tonal,
            )
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun GhostIconButtonPreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GhostIconButton(Icons.Default.Settings, onClick = {}, contentDescription = "Settings")
            GhostIconButton(Icons.Default.MoreVert, onClick = {}, contentDescription = "More")
            GhostIconButton(
                icon = Icons.Default.Close,
                onClick = {},
                contentDescription = "Close",
                variant = GhostIconButtonVariant.Tonal,
            )
        }
    }
}
