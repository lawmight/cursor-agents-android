package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    val spacing = LocalSpacing.current
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(spacing.xs))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun SecondaryButtonPreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SecondaryButton(text = "Get an API key", onClick = {}, modifier = Modifier.fillMaxWidth())
            SecondaryButton(text = "Disabled", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun SecondaryButtonPreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SecondaryButton(text = "Get an API key", onClick = {}, modifier = Modifier.fillMaxWidth())
            SecondaryButton(text = "Disabled", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
        }
    }
}
