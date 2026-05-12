package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = false,
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (onClick != null) it.clickable(onClick = onClick) else it }
                .defaultMinSize(minHeight = 56.dp)
                .padding(horizontal = spacing.m, vertical = spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leading != null) {
                Box(
                    modifier = Modifier.defaultMinSize(minWidth = 32.dp),
                    contentAlignment = Alignment.Center,
                ) { leading() }
                Spacer(Modifier.width(spacing.s))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (trailing != null) {
                Spacer(Modifier.width(spacing.s))
                trailing()
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = spacing.m),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun ListItemPreviewLight() {
    PreviewSurface(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            ListItem(
                title = "API keys",
                subtitle = "Personal · key_********",
                leading = { Icon(Icons.Default.Key, null, tint = MaterialTheme.colorScheme.primary) },
                trailing = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                onClick = {},
                showDivider = true,
            )
            ListItem(
                title = "Theme",
                subtitle = "Follow system",
                leading = { Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary) },
                trailing = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                onClick = {},
            )
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun ListItemPreviewDark() {
    PreviewSurface(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            ListItem(
                title = "API keys",
                subtitle = "Personal · key_********",
                leading = { Icon(Icons.Default.Key, null, tint = MaterialTheme.colorScheme.primary) },
                trailing = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                onClick = {},
                showDivider = true,
            )
            ListItem(
                title = "Theme",
                subtitle = "Follow system",
                leading = { Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary) },
                trailing = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                onClick = {},
            )
        }
    }
}
