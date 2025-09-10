package com.judahben149.tala.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.SpeakingMode
import com.judahben149.tala.navigation.components.others.ConversationDetailScreenComponent
import com.judahben149.tala.navigation.components.others.GuidedPracticeScreenComponent
import com.judahben149.tala.navigation.components.others.ConversationListScreenComponent
import com.judahben149.tala.navigation.components.top.HomeScreenComponent
import com.judahben149.tala.navigation.components.others.ProfileScreenComponent
import com.judahben149.tala.navigation.components.others.SettingsScreenComponent
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import com.judahben149.tala.navigation.components.others.SpeakingModeSelectionComponent
import com.judahben149.tala.navigation.components.others.VoicesScreenComponent
import kotlinx.serialization.Serializable

class MainFlowComponent(
    componentContext: ComponentContext,
    private val onSignOut: () -> Unit
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<MainConfiguration>()

    val childStack: Value<ChildStack<*, MainChild>> = childStack(
        source = navigation,
        serializer = MainConfiguration.serializer(),
        initialConfiguration = MainConfiguration.Home,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        configuration: MainConfiguration,
        componentContext: ComponentContext
    ): MainChild = when (configuration) {
        
        is MainConfiguration.Home -> MainChild.Home(
            HomeScreenComponent(
                componentContext = componentContext,
                onNavigateToProfile = { 
                    navigation.pushNew(MainConfiguration.Profile) 
                },
//                onNavigateToSpeak = {
//                    navigation.pushNew(MainConfiguration.Speak)
//                },
                onNavigateToVoices = {
                    navigation.pushNew(MainConfiguration.Voices)
                },
                onNavigateToConversationHistory = {
                    navigation.pushNew(MainConfiguration.ConversationList)
                },
                onNavigateToSettings = {
                    navigation.pushNew(MainConfiguration.Settings)
                },
                onNavigateToSpeakingModeSelection = {
                    navigation.pushNew(MainConfiguration.SpeakingModeSelection)

                }
            )
        )

        is MainConfiguration.Profile -> MainChild.Profile(
            ProfileScreenComponent(
                componentContext = componentContext,
                onSignOut = onSignOut,
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.Settings -> MainChild.Settings(
            SettingsScreenComponent(
                componentContext = componentContext,
                onNavigateToTerms = {},
                onNavigateToSupport = {},
                onNavigateToFeedback = {},
                onNavigateToPrivacyPolicy = {},
                onBackPressed = { navigation.pop() },
                onSignOut = {  },
                onDeleteAccount = {  }
            )
        )

        is MainConfiguration.Voices -> MainChild.Voices(
            VoicesScreenComponent(
                componentContext = componentContext,
                onVoiceSelected = {
                    navigation.pop()
                    navigation.pushNew(MainConfiguration.SpeakingModeSelection)
                },
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.GuidedPractice -> MainChild.GuidedPractice(
            GuidedPracticeScreenComponent(
                componentContext = componentContext,
                onBeginSpeech = { scenario ->
                    navigation.pop()
                    navigation.pushNew(
                        MainConfiguration.Speak(
                        mode = SpeakingMode.GUIDED_PRACTICE,
                        scenarioId = scenario.id
                    )
                    )
                },
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.Speak -> MainChild.Speak(
            SpeakScreenComponent(
                componentContext = componentContext,
                speakingMode = configuration.mode,
                scenario = configuration.scenarioId?.let {
                    GuidedPracticeScenario.getById(it)
                },
                onViewConversationList = {
//                    navigation.pushNew(MainConfiguration.GuidedPractice)
                },
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.SpeakingModeSelection -> MainChild.SpeakingModeSelection(
            SpeakingModeSelectionComponent(
                componentContext = componentContext,
                onFreeSpeak = {
                    navigation.pushNew(
                        MainConfiguration.Speak(
                            mode = SpeakingMode.FREE_SPEAK,
                            scenarioId = null
                        )
                    )
                },
                onGuidedPractice = {
                    navigation.pushNew(MainConfiguration.GuidedPractice)
                },
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.ConversationList -> MainChild.ConversationList(
            ConversationListScreenComponent(
                componentContext = componentContext,
                onConversationSelected = { conversationId ->
                    navigation.pushNew(MainConfiguration.ConversationDetail(conversationId))
                },
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.ConversationDetail -> MainChild.ConversationDetail(
            ConversationDetailScreenComponent(
                componentContext = componentContext,
                conversationId = configuration.conversationId,
                onBackPressed = { navigation.pop() }
            )
        )
    }
}

@Serializable
sealed class MainConfiguration {
    @Serializable
    data object Home : MainConfiguration()

    @Serializable
    data object Profile : MainConfiguration()

    @Serializable
    data object Settings : MainConfiguration()

    @Serializable
    data class Speak(
        val mode: SpeakingMode,
        val scenarioId: String? = null
    ) : MainConfiguration()

    @Serializable
    data object SpeakingModeSelection : MainConfiguration()

    @Serializable
    data object GuidedPractice : MainConfiguration()

    @Serializable
    data object Voices : MainConfiguration()

    @Serializable
    data object ConversationList : MainConfiguration()

    @Serializable
    data class ConversationDetail(
        val conversationId: String
    ) : MainConfiguration()
}

sealed class MainChild {
    data class Home(val component: HomeScreenComponent) : MainChild()
    data class Profile(val component: ProfileScreenComponent) : MainChild()
    data class Settings(val component: SettingsScreenComponent) : MainChild()
    data class Speak(val component: SpeakScreenComponent) : MainChild()
    data class SpeakingModeSelection(val component: SpeakingModeSelectionComponent) : MainChild()
    data class Voices(val component: VoicesScreenComponent) : MainChild()
    data class GuidedPractice(val component: GuidedPracticeScreenComponent) : MainChild()
    data class ConversationList(val component: ConversationListScreenComponent) : MainChild()
    data class ConversationDetail(val component: ConversationDetailScreenComponent) : MainChild()
}