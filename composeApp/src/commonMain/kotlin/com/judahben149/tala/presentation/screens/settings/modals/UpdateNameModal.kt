package com.judahben149.tala.presentation.screens.settings.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.ui.theme.TalaColors

@Composable
fun UpdateNameModal(
    currentName: String,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    colors: TalaColors
) {
    var name by remember { mutableStateOf(currentName) }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        title = {
            Text(
                text = "Update Name",
                color = colors.primaryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isValid = it.isNotBlank() && it.length >= 2
                    },
                    label = {
                        Text(
                            "Name",
                            color = colors.secondaryText
                        )
                    },
                    isError = !isValid,
                    enabled = !isLoading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.textFieldBorder,
                        errorBorderColor = colors.errorText,
                        focusedTextColor = colors.primaryText,
                        unfocusedTextColor = colors.primaryText,
                        focusedContainerColor = colors.textFieldBackground,
                        unfocusedContainerColor = colors.textFieldBackground
                    )
                )
                
                if (!isValid && name.isNotBlank()) {
                    Text(
                        text = "Name must be at least 2 characters",
                        color = colors.errorText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim()) },
                enabled = isValid && name.trim() != currentName.trim() && !isLoading
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = colors.primary,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Updating...",
                            color = colors.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Update",
                        color = colors.primary,
                        fontWeight = FontWeight.Medium
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
