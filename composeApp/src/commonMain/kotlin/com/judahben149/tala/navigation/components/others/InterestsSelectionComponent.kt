package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class InterestsSelectionComponent(
    componentContext: ComponentContext,
    private val onInterestsSelected: (List<String>) -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun selectInterests(interests: List<String>) {
        onInterestsSelected(interests)
    }

    fun goBack() {
        onBackPressed()
    }
}