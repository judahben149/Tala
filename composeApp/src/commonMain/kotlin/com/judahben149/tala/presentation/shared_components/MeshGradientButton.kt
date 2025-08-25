package com.judahben149.tala.presentation.shared_components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.presentation.shared_components.extensions.meshGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MeshGradientButton() {

    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(0) }

    val animatable = remember { Animatable(.1f) }
    LaunchedEffect(state) {
        when (state) {
            1 -> {
                while (true) {
                    animatable.animateTo(.4f, animationSpec = tween(500))
                    animatable.animateTo(.94f, animationSpec = tween(500))
                }
            }

            2 -> {
                animatable.animateTo(-.9f, animationSpec = tween(durationMillis = 900))
            }

            else -> {
                animatable.animateTo(
                    .5f,
                    animationSpec = tween(durationMillis = 900)
                )
            }
        }
    }

    val color = remember { androidx.compose.animation.Animatable(Sky600) }
    LaunchedEffect(state) {
        when (state) {
            1 -> {
                while (true) {
                    color.animateTo(Emerald500, animationSpec = tween(durationMillis = 500))
                    color.animateTo(Sky400, animationSpec = tween(durationMillis = 500))
                }
            }

            2 -> {
                color.animateTo(Red500, animationSpec = tween(durationMillis = 900))
            }

            else -> {
                color.animateTo(Sky500, animationSpec = tween(durationMillis = 900))
            }
        }
    }

    Box(
        Modifier
            .padding(64.dp)
            .clip(CircleShape)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                scope.launch {
                    if (state == 0) {
                        state = 1
                        delay(4000)
                        state = 2
                        delay(2000)
                        state = 0
                    }

                }
            }
            .meshGradient(
                points = listOf(
                    listOf(
                        Offset(0f, 0f) to Zinc800,
                        Offset(.5f, 0f) to Zinc800,
                        Offset(1f, 0f) to Zinc800,
                    ),
                    listOf(
                        Offset(0f, .5f) to Indigo700,
                        Offset(.5f, animatable.value) to Indigo700,
                        Offset(1f, .5f) to Indigo700,
                    ),
                    listOf(
                        Offset(0f, 1f) to color.value,
                        Offset(.5f, 1f) to color.value,
                        Offset(1f, 1f) to color.value,
                    ),
                ),
                resolutionX = 64,
            )

            .animateContentSize(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                )
            )
    ) {
        AnimatedContent(
            targetState = state,
            modifier = Modifier
                .padding(horizontal = 64.dp, vertical = 32.dp)
                .defaultMinSize(minHeight = 52.dp)
                .align(Alignment.Center),
            transitionSpec = {
                slideInVertically(initialOffsetY = { -it }) + fadeIn() togetherWith slideOutVertically(
                    targetOffsetY = { it }) + fadeOut() using SizeTransform(
                    clip = false, sizeAnimationSpec = { _, _ ->
                        spring(
                            stiffness = Spring.StiffnessHigh,
                        )
                    }
                )
            },
            contentAlignment = Alignment.Center
        ) {
            when (it) {
                1 -> {
                    CircularProgressIndicator(
                        Modifier
                            .padding(horizontal = 32.dp)
                            .align(Alignment.Center),
                        color = Slate50,
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round,
                    )
                }

                2 -> {
                    Text(
                        text = "Wrong!",
                        color = Slate50,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                else -> {
                    Text(
                        text = "Log in",
                        color = Slate50,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }
}

val Emerald500 = Color(0xFF10B981)
val Indigo700 = Color(0xFF4338CA)
val Red500 = Color(0xFFEF4444)
val Sky400 = Color(0xFF38BDF8)
val Sky500 = Color(0xFF0EA5E9)
val Sky600 = Color(0xFF0284C7)
val Slate50 = Color(0xFFF8FAFC)
val Zinc800 = Color(0xFF27272A)