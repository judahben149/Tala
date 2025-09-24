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
    val primary = Amber600 or Amber400

    return TalaColors(
        primary = primary,
        primaryButtonBackground = Amber700 or Amber400,
        primaryButtonText = White or Black,
        secondaryButtonBackground = Gray800 or Gray300,
        secondaryButtonText = White or Black,
        disabledButtonBackground = Gray600 or Gray300,
        disabledButtonText = Gray500 or Gray500,
        textFieldBackground = Gray900 or White,
        textFieldBorder = Gray600 or Gray300,
        textFieldPlaceholderText = Gray500 or Gray400,
        textFieldFocusedIndicator = primary,
        textFieldErrorBackground = Red900 or Red100,
        primaryText = White or Black,
        secondaryText = Gray400 or Gray600,
        accentText = Amber500 or Amber600,
        errorText = Red500 or Red600,
        successText = Green500 or Green600,
        appBackground = Black or Gray100,
        cardBackground = Gray900 or White,
        progressBarFilled = primary,
        progressBarEmpty = Gray700 or Gray200,
        iconTint = primary,
        divider = Gray600 or Gray200,
        navigationBarBackground = Black or White,
        floatingActionButton = Teal500 or Teal400
    )
}

