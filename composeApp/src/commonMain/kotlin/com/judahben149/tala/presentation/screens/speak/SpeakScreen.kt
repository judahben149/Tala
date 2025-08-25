package com.judahben149.tala.presentation.screens.speak

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import com.judahben149.tala.presentation.screens.speak.components.BottomControls
import com.judahben149.tala.presentation.screens.speak.components.MainActionButton
import com.judahben149.tala.ui.theme.Black
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.White
import com.judahben149.tala.ui.theme.getTalaColors
import com.judahben149.tala.ui.theme.latoTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeakScreen(
    component: SpeakScreenComponent,
    viewModel: SpeakScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val backgroundColor = if (isSystemInDarkTheme()) Black else White
    val textColor = if (isSystemInDarkTheme()) White else Black
    val colors = getTalaColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        TopBar(
            onCloseClick = { component.goBack() },
            textColor = textColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Error display just below center
//        uiState.error?.let {
//            ErrorDisplay(
//                error = it,
//                colors = colors,
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .offset(y = 100.dp)
//                    .padding(horizontal = 24.dp)
//            )
//        }


        MainActionButton(
            uiState = uiState,
            onClick = viewModel::onButtonClicked,
            colors = colors,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )

        Text(
            text = uiState.buttonLabel,
            color = textColor,
            fontSize = 13.sp,
            fontStyle = latoTypography().bodySmall.fontStyle,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}


@Composable
private fun TopBar(
    onCloseClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onCloseClick
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StateIndicator(
    uiState: SpeakScreenUiState,
    textColor: Color,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = uiState.buttonIcon,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiState.buttonLabel,
            color = textColor,
            fontSize = 24.sp,
            fontStyle = latoTypography().bodySmall.fontStyle,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.buttonAction,
            color = primaryColor,
            fontSize = 16.sp,
            fontStyle = latoTypography().bodySmall.fontStyle,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorDisplay(
    error: String,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.textFieldErrorBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = error,
            color = colors.errorText,
            fontSize = 14.sp,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontStyle = latoTypography().bodySmall.fontStyle
        )
    }
}