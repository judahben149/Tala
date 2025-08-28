package com.judahben149.tala.presentation.screens.signUp.verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.EmailVerificationComponent
import com.judahben149.tala.ui.theme.getTalaColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationScreen(
    userEmail: String,
    component: EmailVerificationComponent,
    viewModel: EmailVerificationViewModel = koinViewModel()
) {
    val verificationState by viewModel.verificationState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    // Navigate to main app when verification complete
    LaunchedEffect(verificationState.canProceed) {
        if (verificationState.canProceed) {
            component.navigateToWelcome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (verificationState.isVerified) Icons.Default.CheckCircle else Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = if (verificationState.isVerified) colors.successText else colors.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (verificationState.isVerified) "Email Verified!" else "Check Your Email",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (verificationState.isVerified)
                "Setting up your profile..."
            else
                "We sent a verification link to\n$userEmail\n\nClick the link to verify your account.",
            fontSize = 16.sp,
            color = colors.secondaryText,
            textAlign = TextAlign.Center
        )

        if (!verificationState.isVerified) {
            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { viewModel.resendVerificationEmail() }) {
                Text(
                    text = "Resend Verification Email",
                    color = colors.primary
                )
            }
        }

        if (verificationState.isVerified) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(color = colors.primary)
        }
    }
}

