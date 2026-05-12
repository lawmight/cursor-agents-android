package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.lawmight.cursoragents.ui.theme.CursorAgentsTheme
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

/**
 * Wraps preview content in [CursorAgentsTheme] + a tinted [Surface] so previews
 * always show the right surface color, fonts, and composition locals (spacing,
 * status colors, etc.) without each preview reinventing the boilerplate.
 */
@Composable
internal fun PreviewSurface(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    CursorAgentsTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(LocalSpacing.current.m)) { content() }
        }
    }
}
