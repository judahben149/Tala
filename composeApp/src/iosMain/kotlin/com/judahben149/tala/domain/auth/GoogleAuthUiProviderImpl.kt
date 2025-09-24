package com.judahben149.tala.domain.auth

import androidx.compose.runtime.Composable
import cocoapods.GoogleSignIn.GIDSignIn
import com.judahben149.tala.domain.interfaces.GoogleAuthProvider
import com.judahben149.tala.domain.interfaces.GoogleAuthUiProvider
import com.judahben149.tala.domain.models.authentication.GoogleUser
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class GoogleAuthUiProviderImpl
//    : GoogleAuthUiProvider
{

//    @OptIn(ExperimentalForeignApi::class)
//    override suspend fun signIn(): GoogleUser? = suspendCoroutine { continuation ->
//        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
//
//        if (rootViewController == null) {
//            continuation.resume(null)
//        } else {
//            GIDSignIn.sharedInstance
//                .signInWithPresentingViewController(rootViewController) { gidSignInResult, nsError ->
//                    nsError?.let { println("Error while signing: $nsError") }
//
//                    val idToken = gidSignInResult?.user?.idToken?.tokenString
//                    val profile = gidSignInResult?.user?.profile
//
//                    if (idToken != null) {
//                        val googleUser = GoogleUser(
//                            idToken = idToken,
//                            displayName = profile?.name ?: "",
//                            email = profile?.email ?: "",
//                            profilePicUrl = profile?.imageURLWithDimension(320u)?.absoluteString
//                        )
//                        continuation.resume(googleUser)
//                    } else {
//                        continuation.resume(null)
//                    }
//                }
//        }
//    }
}

//internal class GoogleAuthProviderImpl : GoogleAuthProvider {
//    @Composable
//    override fun getUiProvider(): GoogleAuthUiProvider = GoogleAuthUiProviderImpl()
//
//    @OptIn(ExperimentalForeignApi::class)
//    override suspend fun signOut() {
//        GIDSignIn.sharedInstance.signOut()
//    }
//}
