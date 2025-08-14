package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class ProfileScreenComponent(
    componentContext: ComponentContext,
    private val onSignOut: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun signOut() {
        onSignOut()
    }

    fun goBack() {
        onBackPressed()
    }
}