package fr.lawmight.cursoragents.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import fr.lawmight.cursoragents.R

private val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val InterFont = GoogleFont("Inter")
private val JetBrainsMonoFont = GoogleFont("JetBrains Mono")

val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Bold),
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(googleFont = JetBrainsMonoFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Medium),
)

val CursorTypography = Typography(
    displayLarge = cursorTextStyle(FontWeight.Normal, 57.sp, 64.sp, (-0.25).sp),
    displayMedium = cursorTextStyle(FontWeight.Normal, 45.sp, 52.sp, 0.sp),
    displaySmall = cursorTextStyle(FontWeight.Normal, 36.sp, 44.sp, 0.sp),
    headlineLarge = cursorTextStyle(FontWeight.Normal, 32.sp, 40.sp, 0.sp),
    headlineMedium = cursorTextStyle(FontWeight.Normal, 28.sp, 36.sp, 0.sp),
    headlineSmall = cursorTextStyle(FontWeight.Normal, 24.sp, 32.sp, 0.sp),
    titleLarge = cursorTextStyle(FontWeight.Normal, 22.sp, 28.sp, 0.sp),
    titleMedium = cursorTextStyle(FontWeight.Medium, 16.sp, 24.sp, 0.15.sp),
    titleSmall = cursorTextStyle(FontWeight.Medium, 14.sp, 20.sp, 0.1.sp),
    bodyLarge = cursorTextStyle(FontWeight.Normal, 16.sp, 24.sp, 0.5.sp),
    bodyMedium = cursorTextStyle(FontWeight.Normal, 14.sp, 20.sp, 0.25.sp),
    bodySmall = cursorTextStyle(FontWeight.Normal, 12.sp, 16.sp, 0.4.sp),
    labelLarge = cursorTextStyle(FontWeight.Medium, 14.sp, 20.sp, 0.1.sp),
    labelMedium = cursorTextStyle(FontWeight.Medium, 12.sp, 16.sp, 0.5.sp),
    labelSmall = cursorTextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

private fun cursorTextStyle(
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit,
): TextStyle = cursorTextStyle(
    fontFamily = InterFontFamily,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

private fun cursorTextStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit,
): TextStyle = TextStyle(
    fontFamily = fontFamily,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)
