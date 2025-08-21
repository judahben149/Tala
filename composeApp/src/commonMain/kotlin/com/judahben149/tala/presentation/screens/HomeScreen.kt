package com.judahben149.tala.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.navigation.components.top.HomeScreenComponent
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    component: HomeScreenComponent,
//    viewModel: HomeScreenViewModel = koinViewModel()
) {

//    val signInTracker: SignInStateTracker = koinInject()
//
    LaunchedEffect("null") {
//        signInTracker.markSignedOut()
        component.navigateToSpeak()
    }


}