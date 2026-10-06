package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = CyanNeon,
  onPrimary = Color(0xFF00363D),
  primaryContainer = Color(0xFF004F58),
  onPrimaryContainer = Color(0xFF97F0FF),
  secondary = VioletSecondary,
  onSecondary = Color(0xFF281854),
  secondaryContainer = Color(0xFF3E2B70),
  onSecondaryContainer = Color(0xFFEADBFF),
  tertiary = EmeraldNeon,
  onTertiary = Color(0xFF003822),
  tertiaryContainer = Color(0xFF005234),
  onTertiaryContainer = Color(0xFF6CF8B0),
  background = DarkBackground,
  onBackground = TextPrimaryDark,
  surface = DarkSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  surfaceContainer = DarkSurfaceContainer,
  outline = DarkBorder,
  error = RoseDanger,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = CyanPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFC7F4FB),
  onPrimaryContainer = Color(0xFF00363D),
  secondary = VioletNeon,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFEDE9FE),
  onSecondaryContainer = Color(0xFF281854),
  tertiary = EmeraldNeon,
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFD1FAE5),
  onTertiaryContainer = Color(0xFF064E3B),
  background = LightBackground,
  onBackground = TextPrimaryLight,
  surface = LightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = TextSecondaryLight,
  surfaceContainer = LightSurfaceContainer,
  outline = LightBorder,
  error = RoseDanger,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek dark cyberpunk theme for awesome visual look
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
