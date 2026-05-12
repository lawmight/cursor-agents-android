package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

enum class MessageAuthor { User, Agent }

@Composable
fun MessageBubble(
    text: String,
    author: MessageAuthor,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val isUser = author == MessageAuthor.User
    val bubbleShape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }
    val bubbleColor: Color = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor: Color = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Column(modifier = Modifier.widthIn(max = 280.dp)) {
            Text(
                text = if (isUser) "You" else "Agent",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    start = if (isUser) 0.dp else spacing.xs,
                    end = if (isUser) spacing.xs else 0.dp,
                    bottom = spacing.xxs,
                ),
            )
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleColor)
                    .padding(horizontal = spacing.s, vertical = spacing.xs),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                )
            }
        }
    }
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun MessageBubblePreviewLight() {
    PreviewSurface(darkTheme = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            MessageBubble(text = "Refactor the nav host to use type-safe routes.", author = MessageAuthor.User)
            MessageBubble(
                text = "I'll start by inspecting the AppNavHost.kt file and listing the current routes.",
                author = MessageAuthor.Agent,
            )
            MessageBubble(text = "Sounds good — also drop the legacy `agent/{id}` route.", author = MessageAuthor.User)
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun MessageBubblePreviewDark() {
    PreviewSurface(darkTheme = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            MessageBubble(text = "Refactor the nav host to use type-safe routes.", author = MessageAuthor.User)
            MessageBubble(
                text = "I'll start by inspecting the AppNavHost.kt file and listing the current routes.",
                author = MessageAuthor.Agent,
            )
            MessageBubble(text = "Sounds good — also drop the legacy `agent/{id}` route.", author = MessageAuthor.User)
        }
    }
}
