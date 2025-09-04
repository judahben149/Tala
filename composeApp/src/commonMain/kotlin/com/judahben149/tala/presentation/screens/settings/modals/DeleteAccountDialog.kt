package com.judahben149.tala.presentation.screens.settings.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.ui.theme.TalaColors

@Composable
fun DeleteAccountDialog(
    user: AppUser?,
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit,
    colors: TalaColors,
    isLoading: Boolean = false
) {
    var password by remember { mutableStateOf("") }
    val isPasswordRequired = user?.signInMethod == SignInMethod.EMAIL_PASSWORD

    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        title = {
            Text(
                text = "Delete Account?",
                color = colors.primaryText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = if (isPasswordRequired) {
                        "This action cannot be undone. All your progress, conversations, and data will be permanently deleted."
                    } else {
                        "This action cannot be undone. All your progress, conversations, and data will be permanently deleted. Are you sure you want to continue?"
                    },
                    color = colors.secondaryText
                )

                if (isPasswordRequired) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                "Enter your password to confirm",
                                color = colors.secondaryText
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.textFieldBorder,
                            focusedTextColor = colors.primaryText,
                            unfocusedTextColor = colors.primaryText,
                            focusedContainerColor = colors.textFieldBackground,
                            unfocusedContainerColor = colors.textFieldBackground
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(if (isPasswordRequired) password else null) },
                enabled = if (isPasswordRequired) (password.isNotBlank() && !isLoading) else !isLoading
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = colors.errorText,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Deleting...",
                            color = colors.errorText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "Delete",
                        color = colors.errorText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancel",
                    color = colors.secondaryText
                )
            }
        },
        containerColor = colors.cardBackground
    )
}
