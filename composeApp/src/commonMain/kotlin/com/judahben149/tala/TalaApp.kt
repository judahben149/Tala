package com.judahben149.tala

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.navigation.RootComponent
import com.judahben149.tala.presentation.screens.LoginScreen
import com.judahben149.tala.presentation.screens.SignUpScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun TalaApp(
    rootComponent: RootComponent
) {

    MaterialTheme {
        val childStack = rootComponent.childStack.subscribeAsState()

        Children(
            stack = childStack.value,
            animation = stackAnimation(slide())
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.SignUpScreen -> SignUpScreen(instance.component)

                is RootComponent.Child.LoginScreen -> LoginScreen(instance.component)
            }
        }
    }
}