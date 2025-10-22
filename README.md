# Tala - Language Learning Conversation App

[//]: # ([![Android]&#40;https://img.shields.io/github/actions/workflow/status/judahben149/Tala/build.yml?label=Android&branch=master&color=green&#41;]&#40;https://github.com/judahben149/Tala/actions/workflows/build.yml&#41;)

[//]: # ([![iOS]&#40;https://img.shields.io/github/actions/workflow/status/judahben149/Tala/build.yml?label=iOS&branch=master&color=blue&#41;]&#40;https://github.com/judahben149/Tala/actions/workflows/build.yml&#41;)


Tala is a language learning application built with Kotlin Multiplatform that helps users practice conversations in their target language. The app provides an interactive environment where users can engage in voice-based conversations to improve their language skills.

## 📱 Download Tala

### Android
<p align="left">
  <a href="https://play.google.com/store/apps/details?id=com.judahben149.tala_app">
    <img src="https://playbadges.pavi2410.com/badge/full?id=com.judahben149.tala_app" alt="Get it on Google Play" />
  </a>
</p>

### iOS (Coming Soon)
<p align="left">
  <img src="https://img.shields.io/badge/App%20Store-Coming%20Soon-lightgrey?style=for-the-badge&logo=apple" alt="App Store Coming Soon" />
</p>

## Features

- **Voice-based Conversation Practice**: Record your voice and get responses to practice speaking
- **Guided Practice Mode**: Follow structured scenarios to practice specific conversation topics
- **Conversation History**: Review past conversations to track your progress
- **User Profiles**: Track your learning streak and conversation count
- **Multiple Language Support**: Practice in your chosen target language

## Technology Stack

- **Kotlin Multiplatform**: Single codebase for Android and iOS
- **Jetpack Compose**: Modern UI toolkit for building native UI
- **Firebase**: Authentication, data storage, analytics, and remote config
- **SQLite/SQLDelight**: Local database storage
- **Ktor/Ktorfit**: Networking and API communication
- **Speech Recognition**: For processing user's spoken language
- **AI Integration**: Gemini API for natural language processing
- **Audio Processing**: Eleven Labs for voice synthesis

## Libraries

- **Koin**: Dependency injection
- **Decompose**: Navigation and component-based architecture
- **Coil**: Image loading and caching
- **Room**: Database persistence
- **Firebase (Authentication, Database, Remote Config**: Firebase services
- **Ktorfit**: Networking
- **Ktor**: Networking Utils
- **RevenueCat**: In-app purchase management
- **Stream Chat**: Chat functionality
- **Media3/ExoPlayer**: Media playback
- **Accompanist**: UI utilities and permissions handling
- **Kotlinx DateTime**: Date and time handling
- **Kotlinx Serialization**: JSON serialization/deserialization
- **KMP Auth**: Cross-platform authentication

## Project Structure

- **/composeApp**: Contains shared code for all platforms
  - **commonMain**: Core application logic and UI components
  - **androidMain**: Android-specific implementations
  - **iosMain**: iOS-specific implementations
- **/iosApp**: iOS application entry point

## Getting Started

1. Clone the repository
2. Open the project in Android Studio or Xcode
3. Configure Firebase, ElevenLabs, and Gemini credentials
4. Build and run the application

## Learn More

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
