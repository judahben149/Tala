package com.judahben149.tala.presentation.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.judahben149.tala.navigation.components.top.HomeScreenComponent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    component: HomeScreenComponent,
    viewModel: HomeScreenViewModel = koinViewModel()
) {

//    val signInTracker: SignInStateTracker = koinInject()
//
    LaunchedEffect("null") {
//        signInTracker.markSignedOut()

        if (viewModel.isVoicesSelectionComplete()) {
//            component.navigateToSpeak()
            component.navigateToSettings()
        } else {
            component.navigateToVoices()
        }
    }
}