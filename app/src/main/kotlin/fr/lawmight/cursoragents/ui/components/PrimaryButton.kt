package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    val spacing = LocalSpacing.current
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
            )
            Spacer(Modifier.width(spacing.xs))
            Text(text, style = MaterialTheme.typography.labelLarge)
        } else {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(spacing.xs))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun PrimaryButtonPreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PrimaryButton(text = "Launch agent", onClick = {}, modifier = Modifier.fillMaxWidth())
            PrimaryButton(text = "Validating…", onClick = {}, loading = true, modifier = Modifier.fillMaxWidth())
            PrimaryButton(text = "Disabled", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun PrimaryButtonPreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PrimaryButton(text = "Launch agent", onClick = {}, modifier = Modifier.fillMaxWidth())
            PrimaryButton(text = "Validating…", onClick = {}, loading = true, modifier = Modifier.fillMaxWidth())
            PrimaryButton(text = "Disabled", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
        }
    }
}
