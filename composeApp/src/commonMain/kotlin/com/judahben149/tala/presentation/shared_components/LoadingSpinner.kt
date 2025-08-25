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

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier
) {
    val animation = rememberInfiniteTransition()  
    val rotation = animation.animateFloat(  
        initialValue = 0f,  
        targetValue = 360f,  
        animationSpec = infiniteRepeatable(  
            animation = tween(durationMillis = 20_000)  
        ), label = "rotation"  
    )  
    Box(modifier = modifier.graphicsLayer { rotationZ = rotation.value }) {  
        GradientCircle(color = Amber400)
        GradientCircle(color = Amber500, delay = 600)
        GradientCircle(color = Amber600, delay = 1200)
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