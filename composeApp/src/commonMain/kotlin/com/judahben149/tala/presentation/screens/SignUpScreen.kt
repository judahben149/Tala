package com.judahben149.tala.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.judahben149.tala.navigation.components.SignUpScreenComponent
import com.judahben149.tala.presentation.components.TextFieldHint
import com.judahben149.tala.ui.theme.Red400
import com.judahben149.tala.ui.theme.Yellow400
import com.judahben149.tala.ui.theme.or
import com.judahben149.tala.util.isIos

@Composable
fun SignUpScreen(
    component: SignUpScreenComponent
) {
    SignUpScreenContent(
        onSignUpClick = { component.click() },
        onSignUpWithGoogleClick = { component.click() },
        onSignUpWithAppleClick = { component.click() }
    )
}

@Composable
fun SignUpScreenContent(
    onSignUpClick: () -> Unit,
    onSignUpWithGoogleClick: () -> Unit,
    onSignUpWithAppleClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SignUp screen")

        // Email text field
        TextFieldHint(
            hint = "Email"
        )

        // Password text field
        TextFieldHint(
            hint = "Password"
        )

        // Sign up button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = { onSignUpClick() }
        ) {
            Text("Login Screen")
        }

        // Sign up with Google button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = { onSignUpWithGoogleClick() }
        ) {
            Text("Sign up with Google")
        }

        if (isIos()) {
            // Sign up with Google button
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
                onClick = { onSignUpWithAppleClick() }
            ) {
                Text("Sign up with Apple")
            }
        }

    }
}