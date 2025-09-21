package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.firebase.FirebaseService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class RemoteConfigManager(
    private val firebaseService: FirebaseService,
    private val logger: Logger,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    private val _configState = MutableStateFlow<ConfigState>(ConfigState.Loading)
    val configState: StateFlow<ConfigState> = _configState.asStateFlow()
    
    private val _config = MutableStateFlow<Map<String, Any>>(emptyMap())
    val config: StateFlow<Map<String, Any>> = _config.asStateFlow()
    
    sealed class ConfigState {
        object Loading : ConfigState()
        object Success : ConfigState()
        data class Error(val exception: Throwable) : ConfigState()
    }
    
    // Cache for individual config values
    private val configCache = mutableMapOf<String, Any>()
    
    init {
        // Automatically fetch config when manager is initialized
        fetchConfig()
    }
    
    fun fetchConfig() {
        scope.launch {
            try {
                _configState.value = ConfigState.Loading
                logger.d { "Fetching remote config..." }
                
                val remoteConfig = firebaseService.fetchRemoteConfig()
                configCache.clear()
                configCache.putAll(remoteConfig)
                
                _config.value = remoteConfig
                _configState.value = ConfigState.Success
                
                logger.d { "Remote config fetched successfully. Keys: ${remoteConfig.keys}" }
            } catch (e: Exception) {
                logger.e(e) { "Error fetching remote config: ${e.message}" }
                _configState.value = ConfigState.Error(e)
            }
        }
    }
    
    // Convenience methods for getting config values
    suspend fun getString(key: String, defaultValue: String = ""): String {
        return try {
            firebaseService.getRemoteConfigString(key, defaultValue).also {
                logger.d { "Fetched string config for key: $key, value: $it" }
            }
        } catch (e: Exception) {
            logger.e(e) { "Error getting string config for key: $key" }
            defaultValue
        }
    }
    
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return try {
            firebaseService.getRemoteConfigBoolean(key, defaultValue)
        } catch (e: Exception) {
            logger.e(e) { "Error getting boolean config for key: $key" }
            defaultValue
        }
    }
    
    suspend fun getLong(key: String, defaultValue: Long = 0L): Long {
        return try {
            firebaseService.getRemoteConfigLong(key, defaultValue)
        } catch (e: Exception) {
            logger.e(e) { "Error getting long config for key: $key" }
            defaultValue
        }
    }
    
    suspend fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return try {
            firebaseService.getRemoteConfigDouble(key, defaultValue)
        } catch (e: Exception) {
            logger.e(e) { "Error getting double config for key: $key" }
            defaultValue
        }
    }
    
    // Get config value from cache (synchronous)
    fun getCachedString(key: String, defaultValue: String = ""): String {
        return configCache[key] as? String ?: defaultValue
    }
    
    fun getCachedBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return when (val value = configCache[key]) {
            is Boolean -> value
            is String -> value.toBoolean()
            else -> defaultValue
        }
    }
    
    fun getCachedLong(key: String, defaultValue: Long = 0L): Long {
        return when (val value = configCache[key]) {
            is Long -> value
            is Number -> value.toLong()
            is String -> value.toLongOrNull() ?: defaultValue
            else -> defaultValue
        }
    }
    
    fun getCachedDouble(key: String, defaultValue: Double = 0.0): Double {
        return when (val value = configCache[key]) {
            is Double -> value
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: defaultValue
            else -> defaultValue
        }
    }
    
    // App-specific config helpers
    fun isFeatureEnabled(featureName: String): Boolean {
        return getCachedBoolean("feature_${featureName}_enabled", false)
    }
    
    fun getApiUrl(apiName: String, defaultUrl: String): String {
        return getCachedString("api_${apiName}_url", defaultUrl)
    }
    
    fun getMaxRetries(operationName: String, defaultRetries: Int = 3): Int {
        return getCachedLong("max_retries_$operationName", defaultRetries.toLong()).toInt()
    }
}