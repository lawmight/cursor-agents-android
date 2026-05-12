package fr.lawmight.cursoragents.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.lawmight.cursoragents.R

val InterFamily =
    FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
    )

val JetBrainsMonoFamily =
    FontFamily(
        Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    )

// Material 3 type scale tuned for 360-411dp phone widths.
// Display reserved for the brand mark / hero copy; headline for screen titles;
// title for cards + section headers; body for prose; label for badges, chips, captions.
val CursorTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp,
                lineHeight = 48.sp,
                letterSpacing = (-0.5).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.25).sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = 0.sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.1.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.15.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.2.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.6.sp,
            ),
    )
