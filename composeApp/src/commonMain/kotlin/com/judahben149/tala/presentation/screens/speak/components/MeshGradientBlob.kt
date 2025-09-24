package com.judahben149.tala.presentation.screens.speak.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PulsatingShapeAnimation(
    modifier: Modifier = Modifier,
    voiceLevel: Float = 0.3f // Default for testing, 0.0f to 1.0f
) {
    val isDark = isSystemInDarkTheme()

    // Soft, beautiful colors
    val colors = if (isDark) {
        listOf(
            Color(0xFF4DD0E1), // Soft Cyan
            Color(0xFF26A69A), // Teal
            Color(0xFF66BB6A), // Soft Green
            Color(0xFF42A5F5)  // Light Blue
        )
    } else {
        listOf(
            Color(0xFF26C6DA), // Cyan
            Color(0xFF26A69A), // Teal
            Color(0xFF66BB6A), // Green
            Color(0xFF29B6F6)  // Blue
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ultra_smooth_blob")

    // Very slow, graceful animations for liquid-like movement
    val morphPhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "morph1"
    )

    val morphPhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "morph2"
    )

    val breathingPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breathing"
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorShift"
    )

    // Smooth voice level animation
    val animatedVoiceLevel by animateFloatAsState(
        targetValue = voiceLevel,
        animationSpec = tween(300),
        label = "voice_level"
    )

    Canvas(modifier = modifier) {
        drawUltraSmoothBlob(
            colors = colors,
            morphPhase1 = morphPhase1,
            morphPhase2 = morphPhase2,
            breathingPhase = breathingPhase,
            colorShift = colorShift,
            voiceLevel = animatedVoiceLevel
        )
    }
}

private fun DrawScope.drawUltraSmoothBlob(
    colors: List<Color>,
    morphPhase1: Float,
    morphPhase2: Float,
    breathingPhase: Float,
    colorShift: Float,
    voiceLevel: Float
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val baseRadius = size.width * 0.25f

    // Very gentle breathing and voice scaling
    val breathingScale = 1f + 0.05f * sin(breathingPhase)
    val voiceScale = 1f + voiceLevel * 0.3f
    val totalScale = breathingScale * voiceScale

    // Generate smooth blob using mathematical curves
    val blobPath = createUltraSmoothBlobPath(
        centerX = centerX,
        centerY = centerY,
        baseRadius = baseRadius * totalScale,
        morphPhase1 = morphPhase1,
        morphPhase2 = morphPhase2,
        voiceLevel = voiceLevel
    )

    // Soft, tapered gradient colors
    val gradientColors = generateSoftColors(colors, colorShift, voiceLevel)

    // Draw with heavy emphasis on soft, tapered edges
    drawTaperedBlobLayers(
        path = blobPath,
        colors = gradientColors,
        centerX = centerX,
        centerY = centerY,
        baseRadius = baseRadius * totalScale,
        morphPhase1 = morphPhase1,
        morphPhase2 = morphPhase2,
        voiceLevel = voiceLevel
    )
}

private fun createUltraSmoothBlobPath(
    centerX: Float,
    centerY: Float,
    baseRadius: Float,
    morphPhase1: Float,
    morphPhase2: Float,
    voiceLevel: Float
): Path {
    return Path().apply {
        // Use many more points and mathematical smoothing
        val resolution = 120 // Very high resolution for ultra-smooth curves
        val angleStep = 2f * PI.toFloat() / resolution

        // First pass: Calculate all radius points with ultra-smooth morphing
        val radiusPoints = FloatArray(resolution)

        for (i in 0 until resolution) {
            val angle = i * angleStep

            // Multiple harmonics for natural, organic morphing
            val harmonic1 = sin(morphPhase1 * 0.8f + angle * 1.2f)
            val harmonic2 = cos(morphPhase2 * 0.6f + angle * 1.8f)
            val harmonic3 = sin(morphPhase1 * 0.4f + morphPhase2 * 0.3f + angle * 0.7f)
            val harmonic4 = cos(morphPhase1 * 0.9f + angle * 2.3f)

            // Very subtle variations for liquid-like movement
            val morphing = 0.08f * harmonic1 +
                    0.06f * harmonic2 +
                    0.04f * harmonic3 +
                    0.03f * harmonic4

            radiusPoints[i] = baseRadius * (1f + morphing * (0.3f + voiceLevel * 0.4f))
        }

        // Second pass: Apply smoothing filter to eliminate any sharp transitions
        val smoothedRadius = FloatArray(resolution)
        val kernelSize = 8 // Smoothing kernel

        for (i in 0 until resolution) {
            var sum = 0f
            var weight = 0f

            for (j in -kernelSize..kernelSize) {
                val index = (i + j + resolution) % resolution
                val gaussian = kotlin.math.exp(-j * j / (2.0 * 2.0 * 2.0)).toFloat()
                sum += radiusPoints[index] * gaussian
                weight += gaussian
            }
            smoothedRadius[i] = sum / weight
        }

        // Third pass: Create ultra-smooth path using cardinal splines
        val points = mutableListOf<Offset>()
        for (i in 0 until resolution) {
            val angle = i * angleStep
            val radius = smoothedRadius[i]
            points.add(Offset(
                centerX + radius * cos(angle),
                centerY + radius * sin(angle)
            ))
        }

        // Create path with cardinal spline interpolation
        moveTo(points[0].x, points[0].y)

        val tension = 0.3f // Lower tension for smoother curves

        for (i in 0 until resolution) {
            val p0 = points[(i - 1 + resolution) % resolution]
            val p1 = points[i]
            val p2 = points[(i + 1) % resolution]
            val p3 = points[(i + 2) % resolution]

            // Cardinal spline control points
            val cp1x = p1.x + (p2.x - p0.x) * tension
            val cp1y = p1.y + (p2.y - p0.y) * tension
            val cp2x = p2.x - (p3.x - p1.x) * tension
            val cp2y = p2.y - (p3.y - p1.y) * tension

            cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }
        close()
    }
}

private fun generateSoftColors(
    colors: List<Color>,
    colorShift: Float,
    voiceLevel: Float
): List<Color> {
    return colors.mapIndexed { index, baseColor ->
        val shiftedIndex = (index + colorShift * colors.size) % colors.size
        val currentIndex = shiftedIndex.toInt()
        val nextIndex = (currentIndex + 1) % colors.size
        val lerpFactor = shiftedIndex - currentIndex

        val currentColor = colors[currentIndex]
        val nextColor = colors[nextIndex]

        Color(
            red = currentColor.red + (nextColor.red - currentColor.red) * lerpFactor,
            green = currentColor.green + (nextColor.green - currentColor.green) * lerpFactor,
            blue = currentColor.blue + (nextColor.blue - currentColor.blue) * lerpFactor,
            alpha = currentColor.alpha
        )
    }
}

private fun DrawScope.drawTaperedBlobLayers(
    path: Path,
    colors: List<Color>,
    centerX: Float,
    centerY: Float,
    baseRadius: Float,
    morphPhase1: Float,
    morphPhase2: Float,
    voiceLevel: Float
) {
    // Multiple soft, tapered layers for ultra-smooth edges

    // Layer 1: Very large, ultra-soft outer glow (barely visible)
    val veryOuterGlow = Brush.radialGradient(
        colors = listOf(
            colors[0].copy(alpha = 0.05f + voiceLevel * 0.02f),
            Color.Transparent
        ),
        center = Offset(centerX, centerY),
        radius = baseRadius * 4f
    )

    drawCircle(
        brush = veryOuterGlow,
        radius = baseRadius * 3.2f,
        center = Offset(centerX, centerY)
    )

    // Layer 2: Large soft glow
    val outerGlow = Brush.radialGradient(
        colors = listOf(
            colors[0].copy(alpha = 0.12f + voiceLevel * 0.05f),
            colors[1].copy(alpha = 0.06f + voiceLevel * 0.03f),
            Color.Transparent
        ),
        center = Offset(centerX, centerY),
        radius = baseRadius * 2.8f
    )

    drawCircle(
        brush = outerGlow,
        radius = baseRadius * 2.3f,
        center = Offset(centerX, centerY)
    )

    // Layer 3: Medium glow with shape
    val mediumGlow = Brush.radialGradient(
        colors = listOf(
            colors[0].copy(alpha = 0.25f + voiceLevel * 0.1f),
            colors[1].copy(alpha = 0.15f + voiceLevel * 0.05f),
            Color.Transparent
        ),
        center = Offset(centerX, centerY),
        radius = baseRadius * 1.8f
    )

    drawPath(
        path = path,
        brush = mediumGlow
    )

    // Layer 4: Main blob shape with soft gradient
    val gradientCenter = Offset(
        centerX + sin(morphPhase1 * 0.2f) * 8f,
        centerY + cos(morphPhase2 * 0.15f) * 6f
    )

    val mainBrush = Brush.radialGradient(
        colors = listOf(
            colors[0].copy(alpha = 0.8f + voiceLevel * 0.1f),
            colors[1].copy(alpha = 0.7f + voiceLevel * 0.1f),
            colors[2].copy(alpha = 0.5f + voiceLevel * 0.1f),
            colors[3].copy(alpha = 0.3f + voiceLevel * 0.1f),
            colors[0].copy(alpha = 0.1f),
            Color.Transparent
        ),
        center = gradientCenter,
        radius = baseRadius * 1.4f
    )

    drawPath(
        path = path,
        brush = mainBrush
    )

    // Layer 5: Soft inner core
    val innerCore = Brush.radialGradient(
        colors = listOf(
            colors[1].copy(alpha = 0.6f + voiceLevel * 0.2f),
            colors[2].copy(alpha = 0.4f + voiceLevel * 0.1f),
            Color.Transparent
        ),
        center = gradientCenter,
        radius = baseRadius * 0.8f
    )

    drawPath(
        path = path,
        brush = innerCore
    )

    // Layer 6: Subtle highlight
    val highlight = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f + voiceLevel * 0.1f),
            Color.Transparent
        ),
        center = Offset(
            centerX - sin(morphPhase2 * 0.3f) * 12f,
            centerY - cos(morphPhase1 * 0.2f) * 10f
        ),
        radius = baseRadius * 0.5f
    )

    drawPath(
        path = path,
        brush = highlight
    )
}