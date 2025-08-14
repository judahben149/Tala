package com.judahben149.tala.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.navigation.components.others.LoginScreenComponent
import com.judahben149.tala.navigation.components.others.SignUpScreenComponent
import com.judahben149.tala.navigation.components.others.LanguageSelectionComponent
import com.judahben149.tala.navigation.components.others.InterestsSelectionComponent
import com.judahben149.tala.navigation.components.others.WelcomeScreenComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OnboardingFlowComponent(
    componentContext: ComponentContext,
    private val onOnboardingCompleted: () -> Unit
) : ComponentContext by componentContext, KoinComponent {

    private val signInStateTracker: SignInStateTracker by inject()
    private val navigation = StackNavigation<OnboardingConfiguration>()

    val childStack: Value<ChildStack<*, OnboardingChild>> = childStack(
        source = navigation,
        serializer = OnboardingConfiguration.serializer(),
        initialConfiguration = getInitialScreen(),
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun getInitialScreen(): OnboardingConfiguration {
        return if (signInStateTracker.hasSignedInBefore()) {
            OnboardingConfiguration.Login
        } else {
            OnboardingConfiguration.SignUp
        }
    }

    private fun createChild(
        configuration: OnboardingConfiguration,
        componentContext: ComponentContext
    ): OnboardingChild = when (configuration) {

        is OnboardingConfiguration.SignUp -> OnboardingChild.SignUp(
            SignUpScreenComponent(
                componentContext = componentContext,
                onNavigateToLogin = { navigation.bringToFront(OnboardingConfiguration.Login) },
                onSignUpSuccess = ::handleSignUpSuccess
            )
        )

        is OnboardingConfiguration.Login -> OnboardingChild.Login(
            LoginScreenComponent(
                componentContext = componentContext,
                onNavigateToSignUp = { navigation.bringToFront(OnboardingConfiguration.SignUp) },
                onLoginSuccess = ::handleLoginSuccess,
                onBackPressed = ::handleBackPressed
            )
        )

        is OnboardingConfiguration.Welcome -> OnboardingChild.Welcome(
            WelcomeScreenComponent(
                componentContext = componentContext,
                onContinue = { navigation.pushNew(OnboardingConfiguration.LanguageSelection) }
            )
        )

        is OnboardingConfiguration.LanguageSelection -> OnboardingChild.LanguageSelection(
            LanguageSelectionComponent(
                componentContext = componentContext,
                onLanguageSelected = { selectedLanguages ->
                    // Save languages and continue
                    navigation.pushNew(OnboardingConfiguration.InterestsSelection)
                },
                onBackPressed = { navigation.pop() }
            )
        )

        is OnboardingConfiguration.InterestsSelection -> OnboardingChild.InterestsSelection(
            InterestsSelectionComponent(
                componentContext = componentContext,
                onInterestsSelected = { selectedInterests ->
                    // Save interests and complete onboarding
                    completeOnboarding()
                },
                onBackPressed = { navigation.pop() }
            )
        )
    }

    private fun handleSignUpSuccess() {
        // For new users, show welcome and additional setup
        navigation.pushNew(OnboardingConfiguration.Welcome)
    }

    private fun handleLoginSuccess() {
        // For existing users, go straight to main app
        completeOnboarding()
    }

    private fun handleBackPressed() {
        navigation.pop { isSuccess ->
            // Can't go back further, handle as needed
        }
    }

    private fun completeOnboarding() {
        onOnboardingCompleted()
    }

    @Serializable
    sealed class OnboardingConfiguration {
        @Serializable
        data object SignUp : OnboardingConfiguration()

        @Serializable
        data object Login : OnboardingConfiguration()

        @Serializable
        data object Welcome : OnboardingConfiguration()

        @Serializable
        data object LanguageSelection : OnboardingConfiguration()

        @Serializable
        data object InterestsSelection : OnboardingConfiguration()
    }

    sealed class OnboardingChild {
        data class SignUp(val component: SignUpScreenComponent) : OnboardingChild()
        data class Login(val component: LoginScreenComponent) : OnboardingChild()
        data class Welcome(val component: WelcomeScreenComponent) : OnboardingChild()
        data class LanguageSelection(val component: LanguageSelectionComponent) : OnboardingChild()
        data class InterestsSelection(val component: InterestsSelectionComponent) : OnboardingChild()
    }
}