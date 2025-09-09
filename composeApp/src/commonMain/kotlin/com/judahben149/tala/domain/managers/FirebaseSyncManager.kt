package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase
import com.judahben149.tala.domain.usecases.user.PersistUserDataUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class FirebaseSyncManager(
    private val firebaseService: FirebaseService,
    private val persistUserDataUseCase: PersistUserDataUseCase,
    private val observePersistedUserDataUseCase: ObservePersistedUserDataUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    private var syncJob: Job? = null
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    sealed class SyncState {
        object Idle : SyncState()
        object Syncing : SyncState()
        data class Error(val exception: Throwable) : SyncState()
    }

    fun startSyncing() {
        stopSyncing() // Stop any existing sync
        
        syncJob = scope.launch {
            sessionManager.appState
                .filter { it == SessionManager.AppState.LoggedIn }
                .collectLatest { 
                    val userId = sessionManager.getUserId()
                    if (userId.isNotEmpty()) {
                        observeUserProfileChanges(userId)
                    }
                }
        }
    }

    private suspend fun observeUserProfileChanges(userId: String) {
        try {
            _syncState.value = SyncState.Syncing
            logger.d { "Starting Firebase sync for user: $userId" }
            
            firebaseService.observeUserProfile(userId)
                .filterNotNull()
                .distinctUntilChanged()
                .collect { firebaseData ->
                    try {
                        syncFirebaseDataToLocal(userId, firebaseData)
                    } catch (e: Exception) {
                        logger.e(e) { "Error syncing Firebase data to local: ${e.message}" }
                        _syncState.value = SyncState.Error(e)
                    }
                }
        } catch (e: Exception) {
            logger.e(e) { "Error in Firebase sync: ${e.message}" }
            _syncState.value = SyncState.Error(e)
        }
    }

    private suspend fun syncFirebaseDataToLocal(userId: String, firebaseData: Map<String, Any>) {
        try {
            // Get current local user data
            val currentLocalUser = if (observePersistedUserDataUseCase.hasPersistedUser()) {
                observePersistedUserDataUseCase.getCurrentUser()
            } else {
                null
            }
            
            // Convert Firebase data to AppUser
            val updatedUser = mapFirebaseDataToAppUser(userId, firebaseData, currentLocalUser)
            
            // Only update if there are actual changes
            if (currentLocalUser == null || hasSignificantChanges(currentLocalUser, updatedUser)) {
                persistUserDataUseCase(updatedUser)
                logger.d { "User data synced from Firebase for user: $userId" }
            }
            
            _syncState.value = SyncState.Idle
        } catch (e: Exception) {
            logger.e(e) { "Error updating local user data: ${e.message}" }
            throw e
        }
    }

    private fun mapFirebaseDataToAppUser(
        userId: String, 
        firebaseData: Map<String, Any>,
        currentUser: AppUser?
    ): AppUser {
        return AppUser(
            userId = userId,
            displayName = firebaseData["displayName"] as? String 
                ?: firebaseData["name"] as? String 
                ?: currentUser?.displayName 
                ?: "Unknown",
            email = firebaseData["email"] as? String 
                ?: currentUser?.email 
                ?: "Unknown",
            isPremiumUser = firebaseData["isPremiumUser"] as? Boolean 
                ?: currentUser?.isPremiumUser 
                ?: false,
            firstName = firebaseData["firstName"] as? String 
                ?: currentUser?.firstName 
                ?: "",
            lastName = firebaseData["lastName"] as? String 
                ?: currentUser?.lastName 
                ?: "",
            avatarUrl = firebaseData["avatarUrl"] as? String 
                ?: currentUser?.avatarUrl,
            isEmailVerified = firebaseData["emailVerified"] as? Boolean
                ?: currentUser?.isEmailVerified
                ?: false,
            createdAt = (firebaseData["createdAt"] as? Number)?.toLong() 
                ?: currentUser?.createdAt 
                ?: 0L,
            updatedAt = getCurrentTimeMillis(),
            signInMethod = SignInMethod.valueOf(firebaseData["signInMethod"] as? String
                ?: currentUser?.signInMethod?.name
                ?: "EMAIL_PASSWORD"
            ),
            
            // Learning Progress Fields
            streakDays = (firebaseData["streakDays"] as? Number)?.toInt() 
                ?: currentUser?.streakDays 
                ?: 0,
            totalConversations = (firebaseData["totalConversations"] as? Number)?.toInt() 
                ?: currentUser?.totalConversations 
                ?: 0,
            learningLanguage = firebaseData["learningLanguage"] as? String 
                ?: currentUser?.learningLanguage 
                ?: "ENGLISH",
            interests = (firebaseData["interests"] as? List<*>)?.filterIsInstance<String>() 
                ?: currentUser?.interests 
                ?: emptyList(),
            currentLevel = firebaseData["currentLevel"] as? String 
                ?: currentUser?.currentLevel 
                ?: "Beginner",
            totalPoints = (firebaseData["totalPoints"] as? Number)?.toInt() 
                ?: currentUser?.totalPoints 
                ?: 0,
            weeklyGoal = (firebaseData["weeklyGoal"] as? Number)?.toInt() 
                ?: currentUser?.weeklyGoal 
                ?: 7,
            achievementBadges = (firebaseData["achievementBadges"] as? List<*>)?.filterIsInstance<String>() 
                ?: currentUser?.achievementBadges 
                ?: emptyList(),
            
            // App Preferences
            notificationsEnabled = firebaseData["notificationsEnabled"] as? Boolean 
                ?: currentUser?.notificationsEnabled 
                ?: true,
            practiceRemindersEnabled = firebaseData["practiceRemindersEnabled"] as? Boolean 
                ?: currentUser?.practiceRemindersEnabled 
                ?: true,
            selectedVoiceId = firebaseData["selectedVoiceId"] as? String 
                ?: currentUser?.selectedVoiceId,
            preferredDifficulty = firebaseData["preferredDifficulty"] as? String 
                ?: currentUser?.preferredDifficulty 
                ?: "Medium",
            dailyGoalMinutes = (firebaseData["dailyGoalMinutes"] as? Number)?.toInt() 
                ?: currentUser?.dailyGoalMinutes 
                ?: 15,
            
            // Social Features
            friendsCount = (firebaseData["friendsCount"] as? Number)?.toInt() 
                ?: currentUser?.friendsCount 
                ?: 0,
            isPrivateProfile = firebaseData["isPrivateProfile"] as? Boolean 
                ?: currentUser?.isPrivateProfile 
                ?: false,
            bio = firebaseData["bio"] as? String 
                ?: currentUser?.bio 
                ?: "",
            location = firebaseData["location"] as? String 
                ?: currentUser?.location,
            timezone = firebaseData["timezone"] as? String 
                ?: currentUser?.timezone,
            
            // App Statistics
            totalStudyTimeMinutes = (firebaseData["totalStudyTimeMinutes"] as? Number)?.toLong() 
                ?: currentUser?.totalStudyTimeMinutes 
                ?: 0L,
            favoriteTopics = (firebaseData["favoriteTopics"] as? List<*>)?.filterIsInstance<String>() 
                ?: currentUser?.favoriteTopics 
                ?: emptyList(),
            lastActiveAt = (firebaseData["lastActiveAt"] as? Number)?.toLong() 
                ?: currentUser?.lastActiveAt 
                ?: 0L,
            loginCount = (firebaseData["loginCount"] as? Number)?.toInt() 
                ?: currentUser?.loginCount 
                ?: 0,
            onboardingCompleted = firebaseData["onboardingCompleted"] as? Boolean 
                ?: currentUser?.onboardingCompleted 
                ?: false
        )
    }

    private fun hasSignificantChanges(currentUser: AppUser, newUser: AppUser): Boolean {
        // Define which fields constitute significant changes that warrant a local update
        return currentUser.displayName != newUser.displayName ||
                currentUser.email != newUser.email ||
                currentUser.isPremiumUser != newUser.isPremiumUser ||
                currentUser.signInMethod != newUser.signInMethod ||
                currentUser.firstName != newUser.firstName ||
                currentUser.lastName != newUser.lastName ||
                currentUser.avatarUrl != newUser.avatarUrl ||
                currentUser.isEmailVerified != newUser.isEmailVerified ||
                currentUser.createdAt != newUser.createdAt ||
                currentUser.updatedAt != newUser.updatedAt ||

                // Learning Progress Fields
                currentUser.streakDays != newUser.streakDays ||
                currentUser.totalConversations != newUser.totalConversations ||
                currentUser.learningLanguage != newUser.learningLanguage ||
                currentUser.interests != newUser.interests ||
                currentUser.currentLevel != newUser.currentLevel ||
                currentUser.totalPoints != newUser.totalPoints ||
                currentUser.weeklyGoal != newUser.weeklyGoal ||
                currentUser.achievementBadges != newUser.achievementBadges ||

                // App Preferences
                currentUser.notificationsEnabled != newUser.notificationsEnabled ||
                currentUser.practiceRemindersEnabled != newUser.practiceRemindersEnabled ||
                currentUser.selectedVoiceId != newUser.selectedVoiceId ||
                currentUser.preferredDifficulty != newUser.preferredDifficulty ||
                currentUser.dailyGoalMinutes != newUser.dailyGoalMinutes ||

                // Social Features (for future use)
                currentUser.friendsCount != newUser.friendsCount ||
                currentUser.isPrivateProfile != newUser.isPrivateProfile ||
                currentUser.bio != newUser.bio ||
                currentUser.location != newUser.location ||
                currentUser.timezone != newUser.timezone ||

                // App Statistics
                currentUser.totalStudyTimeMinutes != newUser.totalStudyTimeMinutes ||
                currentUser.favoriteTopics != newUser.favoriteTopics ||
                currentUser.lastActiveAt != newUser.lastActiveAt ||
                currentUser.loginCount != newUser.loginCount ||
                currentUser.onboardingCompleted != newUser.onboardingCompleted
    }

    fun stopSyncing() {
        syncJob?.cancel()
        syncJob = null
        _syncState.value = SyncState.Idle
        logger.d { "Firebase sync stopped" }
    }

    fun forceSyncNow() {
        scope.launch {
            val userId = sessionManager.getUserId()
            if (userId.isNotEmpty()) {
                try {
                    _syncState.value = SyncState.Syncing
                    val firebaseData = firebaseService.fetchUserProfile(userId)
                    if (firebaseData != null) {
                        syncFirebaseDataToLocal(userId, firebaseData)
                        logger.d { "Force sync completed for user: $userId" }
                    }
                } catch (e: Exception) {
                    logger.e(e) { "Error in force sync: ${e.message}" }
                    _syncState.value = SyncState.Error(e)
                }
            }
        }
    }
}