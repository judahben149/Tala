package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class LanguageSelectionComponent(
    componentContext: ComponentContext,
    private val onLanguageSelected: (List<String>) -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun selectLanguages(languages: List<String>) {
        onLanguageSelected(languages)
    }

    fun goBack() {
        onBackPressed()
    }
}
