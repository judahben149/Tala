# Tala - Compose Multiplatform App

A clean, idiomatic Kotlin Compose Multiplatform app targeting Android and iOS with a comprehensive architecture.

## Features

### ✅ Implemented
- **Authentication**: Sign in with Google and Apple (mock implementation)
- **Chat**: Basic chat UI with mock messages
- **Navigation**: Decompose-based navigation with screen state management
- **Dependency Injection**: Koin for multiplatform DI
- **Local Storage**: Room database and DataStore preferences
- **Architecture**: Clean Architecture with proper separation of concerns
- **Concurrency**: Kotlin Coroutines and Flow for reactive state management

### 🏗️ Architecture

```
com.judahben149.tala/
├── domain/                    # Domain layer
│   ├── model/                 # Domain models
│   ├── repository/            # Repository interfaces
│   └── usecase/              # Use cases
├── data/                      # Data layer
│   ├── local/                 # Local storage (Room, DataStore)
│   ├── model/                 # Data models
│   └── repository/            # Repository implementations
├── presentation/              # Presentation layer
│   ├── navigation/            # Decompose navigation components
│   └── screen/               # UI screens
└── di/                       # Dependency injection
```

## Tech Stack

- **UI**: Compose Multiplatform
- **Navigation**: Decompose
- **DI**: Koin
- **Database**: Room Multiplatform
- **Preferences**: DataStore
- **Authentication**: Firebase Auth (Android), Apple Sign In (iOS)
- **Chat**: GetStream Chat SDK
- **Concurrency**: Kotlin Coroutines + Flow
- **DateTime**: kotlinx-datetime

## Project Structure

### Domain Layer
- `User`, `ChatMessage`, `TestEntity` models
- Repository interfaces for auth, chat, and storage
- Use cases for business logic

### Data Layer
- Room database with DAOs
- DataStore for key-value storage
- Repository implementations with platform-specific code
- Firebase Auth integration (Android)
- GetStream Chat integration (Android)

### Presentation Layer
- Decompose navigation components
- Material 3 UI screens
- State management with Flow

### Test Screens
- **RoomTestScreen**: Test Room database operations
- **PrefsTestScreen**: Test DataStore operations

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on Android or iOS

## TODO

- [ ] Implement Firebase Auth for Android
- [ ] Implement Apple Sign In for iOS
- [ ] Integrate GetStream Chat SDK
- [ ] Add proper error handling
- [ ] Add unit tests
- [ ] Add UI tests
- [ ] Implement proper context injection for Android
- [ ] Complete iOS database implementation

## Notes

- All code is clean and idiomatic Kotlin
- Uses latest stable versions of all libraries
- Follows Clean Architecture principles
- Single module structure for simplicity
- Platform-specific code uses expect/actual pattern