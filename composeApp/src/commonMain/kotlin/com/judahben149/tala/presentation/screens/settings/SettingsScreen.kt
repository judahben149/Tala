package com.judahben149.tala.presentation.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.judahben149.tala.domain.models.session.PasswordUpdateData
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.navigation.components.others.SettingsScreenComponent
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel
import tala.composeapp.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    component: SettingsScreenComponent,
    viewModel: SettingsScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        TopBar(
            onBackClick = { component.goBack() },
            colors = colors
        )

        // Profile Section
        uiState.user?.let { user ->
            ProfileSection(
                user = user,
                colors = colors,
                onEditProfileClick = { /* TODO: Open edit profile dialog */ }
            )
        }

        // Account Settings
        SettingsSection(
            title = "Account",
            colors = colors
        ) {
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Update Profile",
                subtitle = "Change your name and email",
                colors = colors,
                onClick = { /* TODO: Open profile editor */ }
            )

            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Update Password",
                subtitle = "Keep your account secure",
                colors = colors,
                onClick = { viewModel.showPasswordDialog() }
            )

            SettingsItem(
                icon = Icons.Default.RecordVoiceOver,
                title = "Change AI Voice",
                subtitle = uiState.user?.selectedVoiceId ?: "Select your preferred voice",
                colors = colors,
                onClick = { viewModel.showVoiceSelector() }
            )

            SettingsItem(
                icon = Icons.Default.Language,
                title = "Learning Language",
                subtitle = uiState.user?.learningLanguage ?: "Select your preferred language",
                colors = colors,
                onClick = { viewModel.showLanguageSelector() }
            )
        }

        // Preferences
        SettingsSection(
            title = "Preferences",
            colors = colors
        ) {
            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Get updates about your progress",
                isChecked = uiState.user?.notificationsEnabled ?: false,
                colors = colors,
                onToggle = { viewModel.toggleNotifications(it) }
            )

            SettingsToggleItem(
                icon = Icons.Default.Schedule,
                title = "Practice Reminders",
                subtitle = "Daily reminders to keep practicing",
                isChecked = uiState.user?.practiceRemindersEnabled ?: false,
                colors = colors,
                onToggle = { viewModel.togglePracticeReminders(it) }
            )
        }

        // Support & Legal
        SettingsSection(
            title = "Support & Legal",
            colors = colors
        ) {
            SettingsItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                subtitle = "Get help with using Tala",
                colors = colors,
                onClick = { component.navigateToSupport() }
            )

            SettingsItem(
                icon = Icons.Default.Policy,
                title = "Privacy Policy",
                subtitle = "How we protect your data",
                colors = colors,
                onClick = { component.navigateToPrivacyPolicy() }
            )

            SettingsItem(
                icon = Icons.Default.Description,
                title = "Terms of Service",
                subtitle = "Terms and conditions",
                colors = colors,
                onClick = { component.navigateToTerms() }
            )

            SettingsItem(
                icon = Icons.Default.Feedback,
                title = "Send Feedback",
                subtitle = "Help us improve Tala",
                colors = colors,
                onClick = { component.navigateToFeedback() }
            )
        }

        // Account Actions
        SettingsSection(
            title = "Account Actions",
            colors = colors
        ) {
            SettingsItem(
                icon = Icons.Default.Logout,
                title = "Sign Out",
                subtitle = "Sign out of your account",
                colors = colors,
                textColor = colors.secondaryText,
                onClick = {
                    viewModel.signOut()
                    component.onSignedOut()
                }
            )

            SettingsItem(
                icon = Icons.Default.DeleteForever,
                title = "Delete Account",
                subtitle = "Permanently delete your account",
                colors = colors,
                textColor = colors.errorText,
                onClick = { viewModel.showDeleteConfirmation() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Loading overlay
    if (uiState.isLoading || uiState.isDeletingAccount) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colors.primary)
        }
    }

    // Error display
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO: Show snackbar or toast with error
        }
    }

    // Dialogs
    if (uiState.showDeleteConfirmation) {
        DeleteAccountDialog(
            onConfirm = { password ->
                viewModel.hideDeleteConfirmation()
                viewModel.deleteAccount(password)
                component.onAccountDeleted()
            },
            onDismiss = { viewModel.hideDeleteConfirmation() },
            colors = colors,
            isLoading = uiState.isDeletingAccount
        )
    }

    if (uiState.showPasswordDialog) {
        PasswordUpdateDialog(
            onConfirm = { passwordData ->
                viewModel.hidePasswordDialog()
                viewModel.updatePassword(passwordData)
            },
            onDismiss = { viewModel.hidePasswordDialog() },
            colors = colors
        )
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    colors: TalaColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.primaryText,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Settings",
            color = colors.primaryText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileSection(
    user: AppUser,
    colors: TalaColors,
    onEditProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colors.appBackground),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(64.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.displayName,
                    color = colors.primaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.email,
                    color = colors.secondaryText,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${user.streakDays} day streak • ${user.totalConversations} conversations",
                    color = colors.accentText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Edit button
            IconButton(onClick = onEditProfileClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = colors.secondaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    colors: TalaColors,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = colors.secondaryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: TalaColors,
    textColor: Color = colors.primaryText,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                color = colors.secondaryText,
                fontSize = 14.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.secondaryText,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    colors: TalaColors,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                color = colors.secondaryText,
                fontSize = 14.sp
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = colors.secondaryText,
                uncheckedTrackColor = colors.secondaryText.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun DeleteAccountDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    colors: TalaColors,
    isLoading: Boolean = false
) {
    var password by remember { mutableStateOf("") }

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
                    text = "This action cannot be undone. All your progress, conversations, and data will be permanently deleted.",
                    color = colors.secondaryText
                )

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
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(password) },
                enabled = password.isNotBlank() && !isLoading
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

@Composable
private fun PasswordUpdateDialog(
    onConfirm: (PasswordUpdateData) -> Unit,
    onDismiss: () -> Unit,
    colors: TalaColors
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Password",
                color = colors.primaryText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = {
                        Text(
                            "Current Password",
                            color = colors.secondaryText
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.textFieldBorder,
                        focusedTextColor = colors.primaryText,
                        unfocusedTextColor = colors.primaryText,
                        focusedContainerColor = colors.textFieldBackground,
                        unfocusedContainerColor = colors.textFieldBackground
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = {
                        Text(
                            "New Password",
                            color = colors.secondaryText
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.textFieldBorder,
                        focusedTextColor = colors.primaryText,
                        unfocusedTextColor = colors.primaryText,
                        focusedContainerColor = colors.textFieldBackground,
                        unfocusedContainerColor = colors.textFieldBackground
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(
                            "Confirm Password",
                            color = colors.secondaryText
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
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
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        PasswordUpdateData(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword
                        )
                    )
                },
                enabled = currentPassword.isNotBlank() &&
                        newPassword.isNotBlank() &&
                        confirmPassword.isNotBlank()
            ) {
                Text(
                    text = "Update",
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = colors.primary
                )
            }
        },
        containerColor = colors.cardBackground
    )
}
