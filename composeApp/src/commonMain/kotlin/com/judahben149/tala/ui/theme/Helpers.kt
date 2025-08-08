package com.judahben149.tala.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

@ReadOnlyComposable
@Composable
infix fun Color.or(light: Color) =
    if (isSystemInDarkTheme()) this else light