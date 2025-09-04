package com.judahben149.tala.domain.auth

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.judahben149.tala.domain.interfaces.GoogleAuthProvider
import com.judahben149.tala.domain.interfaces.GoogleAuthUiProvider
import com.judahben149.tala.domain.models.authentication.GoogleAuthCredentials
import com.judahben149.tala.domain.models.authentication.GoogleUser

internal class GoogleAuthUiProviderImpl(
    private val activityContext: Context,
    private val credentialManager: CredentialManager,
    private val credentials: GoogleAuthCredentials,
) : GoogleAuthUiProvider {

    override suspend fun signIn(): GoogleUser? {
        return try {
            val credential = credentialManager.getCredential(
                context = activityContext,
                request = getCredentialRequest()
            ).credential
            getGoogleUserFromCredential(credential)
        } catch (e: GetCredentialException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun getGoogleUserFromCredential(credential: Credential): GoogleUser? {
        return when {
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    GoogleUser(
                        idToken = googleIdTokenCredential.idToken,
                        displayName = googleIdTokenCredential.displayName ?: "",
                        email = googleIdTokenCredential.id,
                        profilePicUrl = googleIdTokenCredential.profilePictureUri?.toString()
                    )
                } catch (e: GoogleIdTokenParsingException) {
                    null
                }
            }
            else -> null
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(getGoogleIdOption())
            .build()
    }

    private fun getGoogleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setServerClientId(credentials.serverId)
            .build()
    }
}

internal class GoogleAuthProviderImpl(
    private val credentials: GoogleAuthCredentials,
    private val credentialManager: CredentialManager,
) : GoogleAuthProvider {

    @Composable
    override fun getUiProvider(): GoogleAuthUiProvider {
        val activityContext = LocalContext.current
        return GoogleAuthUiProviderImpl(
            activityContext = activityContext,
            credentialManager = credentialManager,
            credentials = credentials
        )
    }

    override suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
