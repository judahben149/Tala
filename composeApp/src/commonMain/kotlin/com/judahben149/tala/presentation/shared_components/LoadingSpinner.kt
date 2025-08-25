package com.judahben149.tala.presentation.shared_components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import com.judahben149.tala.ui.theme.Amber400
import com.judahben149.tala.ui.theme.Amber500
import com.judahben149.tala.ui.theme.Amber600
import com.judahben149.tala.ui.theme.Fuchsia400
import com.judahben149.tala.ui.theme.Fuchsia500
import com.judahben149.tala.ui.theme.Fuchsia600
import com.judahben149.tala.ui.theme.Lime400
import com.judahben149.tala.ui.theme.Lime500
import com.judahben149.tala.ui.theme.Lime600
import com.judahben149.tala.ui.theme.Rose400
import com.judahben149.tala.ui.theme.Rose500
import com.judahben149.tala.ui.theme.Rose600
import com.judahben149.tala.ui.theme.Teal400
import com.judahben149.tala.ui.theme.Teal500
import com.judahben149.tala.ui.theme.Teal600
import com.judahben149.tala.ui.theme.Violet400
import com.judahben149.tala.ui.theme.Violet500
import com.judahben149.tala.ui.theme.Violet600

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    voicePersonaNumber: Int = 1
) {
    val animation = rememberInfiniteTransition()  
    val rotation = animation.animateFloat(  
        initialValue = 0f,  
        targetValue = 360f,  
        animationSpec = infiniteRepeatable(  
            animation = tween(durationMillis = 20_000)  
        ), label = "rotation"  
    )

    val (color1, color2, color3) = getLoadingPersonaColours(voicePersonaNumber)

    Box(modifier = modifier.graphicsLayer { rotationZ = rotation.value }) {  
        GradientCircle(color = color1)
        GradientCircle(color = color2, delay = 600)
        GradientCircle(color = color3, delay = 1200)
    }  
}  
  
@Composable  
fun GradientCircle(  
    modifier: Modifier = Modifier,
    color: Color,
    delay: Int = 0,
) {  
    val animation = rememberInfiniteTransition()  
    val rotation = animation.animateFloat(  
        initialValue = 0f,  
        targetValue = 360f,  
        animationSpec = infiniteRepeatable(  
            animation = tween(durationMillis = 6_000, easing = LinearEasing),
            initialStartOffset = StartOffset(delay)
        ), label = "gradientCircleRotation"  
    )  
    Box(  
        modifier = modifier  
            .graphicsLayer { rotationX = rotation.value; cameraDistance = 100000f }  
            .fillMaxSize()  
            .drawBehind {  
                drawCircle(  
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),  
                        center = Offset.Zero,  
                        radius = size.width,  
                    )  
                )  
                drawCircle(  
                    brush = Brush.radialGradient(  
                        colors = listOf(color, Color.Transparent),  
                        center = Offset.Zero,  
                        radius = size.width * 1.5f,  
                    ),  
                    style = Stroke(width = 1f)
                )  
            }  
    )  
}


fun getLoadingPersonaColours(
    personaNumber: Int
): Triple<Color, Color, Color> {
    return when(personaNumber) {
        1 -> return Triple(Amber400, Amber500, Amber600)
        2 -> return Triple(Teal400, Teal500, Teal600)
        3 -> return Triple(Lime400, Lime500, Lime600)
        4 -> return Triple(Rose400, Rose500, Rose600)
        5 -> return Triple(Violet400, Violet500, Violet600)
        6 -> return Triple(Fuchsia400, Fuchsia500, Fuchsia600)
        else -> Triple(Amber400, Amber500, Amber600)
    }
}