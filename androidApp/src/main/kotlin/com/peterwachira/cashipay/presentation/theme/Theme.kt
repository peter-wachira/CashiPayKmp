package com.peterwachira.cashipay.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CashiPrimary,
    onPrimary = Color.White,
    primaryContainer = CashiMint,
    onPrimaryContainer = CashiInk,
    secondary = CashiPrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = CashiMintSoft,
    onSecondaryContainer = CashiInk,
    background = CashiBackground,
    onBackground = CashiInk,
    surface = CashiSurface,
    onSurface = CashiInk,
    error = CashiError,
    onError = Color.White,
    outline = CashiBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = CashiMint,
    onPrimary = CashiPrimaryDark,
    primaryContainer = CashiPrimary,
    onPrimaryContainer = CashiMintSoft,
    secondary = CashiMint,
    onSecondary = CashiPrimaryDark,
    secondaryContainer = CashiPrimaryDark,
    onSecondaryContainer = CashiMintSoft,
    background = CashiInk,
    onBackground = CashiMintSoft,
    surface = CashiPrimaryDark,
    onSurface = CashiMintSoft,
    error = CashiError,
    onError = Color.White,
    outline = CashiMuted
)

/**
 * Applies CashiPay colours, typography, and shapes to Android Compose content.
 */
@Composable
internal fun CashiPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CashiPayTypography,
        shapes = CashiPayShapes,
        content = content
    )
}