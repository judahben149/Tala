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
import com.judahben149.tala.ui.theme.Red400
import com.judahben149.tala.ui.theme.Yellow400
import com.judahben149.tala.ui.theme.or

@Composable
fun SignUpScreen(
    component: SignUpScreenComponent
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SignUp screen")

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = { component.click() }
        ) {
            Text("Login Screen")
        }
    }
}