package com.judahben149.tala.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

@ReadOnlyComposable
@Composable
infix fun Color.or(light: Color) =
    if (isSystemInDarkTheme()) this else light


data class TalaColors(
    val primary: Color,
    val primaryButtonBackground: Color,
    val primaryButtonText: Color,
    val secondaryButtonBackground: Color,
    val secondaryButtonText: Color,
    val disabledButtonBackground: Color,
    val disabledButtonText: Color,
    val textFieldBackground: Color,
    val textFieldBorder: Color,
    val textFieldPlaceholderText: Color,
    val textFieldFocusedIndicator: Color,
    val textFieldErrorBackground: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accentText: Color,
    val errorText: Color,
    val successText: Color,
    val appBackground: Color,
    val cardBackground: Color,
    val progressBarFilled: Color,
    val progressBarEmpty: Color,
    val iconTint: Color,
    val divider: Color,
    val navigationBarBackground: Color,
    val floatingActionButton: Color
)

@Composable
fun getTalaColors(): TalaColors {
    val primary = Color(0xFFeaaf24) or Color(0xFFfbbf24)

    return TalaColors(
        primary = primary,
        primaryButtonBackground = Amber800 or Amber400,
        primaryButtonText = White or Black,
        secondaryButtonBackground = Orange600 or Orange300,
        secondaryButtonText = White or Slate900,
        disabledButtonBackground = Gray500 or Gray300,
        disabledButtonText = Gray700 or Gray500,
        textFieldBackground = Slate800 or White,
        textFieldBorder = Slate600 or Slate300,
        textFieldPlaceholderText = Gray600 or Gray400,
        textFieldFocusedIndicator = primary,
        textFieldErrorBackground = Red100 or Red50,
        primaryText = Slate50 or Slate950,
        secondaryText = Gray400 or Gray600,
        accentText = Yellow500 or Yellow300,
        errorText = Red600 or Red500,
        successText = Green600 or Green400,
        appBackground = Slate900 or Slate50,
        cardBackground = Slate700 or White,
        progressBarFilled = primary,
        progressBarEmpty = Gray700 or Gray200,
        iconTint = primary,
        divider = Slate600 or Slate200,
        navigationBarBackground = Slate950 or White,
        floatingActionButton = Teal500 or Teal300
    )
}