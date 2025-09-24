package com.judahben149.tala.presentation.shared_components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.judahben149.tala.ui.theme.getTalaColors
import com.judahben149.tala.util.isIos

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    tint: Color = getTalaColors().primaryText,
    contentDescription: String = "Navigate back",
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = if (isIos()) Icons.AutoMirrored.Filled.ArrowBackIos else Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(36.dp)
        )
    }
}