package com.judahben149.tala.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import tala.composeapp.generated.resources.Res
import tala.composeapp.generated.resources.lato_bold
import tala.composeapp.generated.resources.lato_light
import tala.composeapp.generated.resources.lato_regular

@OptIn(ExperimentalResourceApi::class)
@Composable
fun latoFontFamily() = FontFamily(
    Font(Res.font.lato_light, weight = FontWeight.Light),
    Font(Res.font.lato_regular, weight = FontWeight.Normal),
    Font(Res.font.lato_regular, weight = FontWeight.Medium),
    Font(Res.font.lato_bold, weight = FontWeight.SemiBold),
    Font(Res.font.lato_bold, weight = FontWeight.Bold)
)

@Composable
fun latoTypography() = Typography().run {

    val fontFamily = latoFontFamily()

    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}