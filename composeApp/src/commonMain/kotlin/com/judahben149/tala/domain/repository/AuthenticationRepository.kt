package com.judahben149.tala.domain.repository

//import com.judahben149.tala.domain.models.authentication.CreateUserWithEmailAndPasswordError
//import com.judahben149.tala.domain.models.authentication.FirebaseUserInfoDomainModel
//import com.judahben149.tala.domain.models.authentication.LogOutError
//import com.judahben149.tala.domain.models.authentication.ProviderType
//import com.judahben149.tala.domain.models.authentication.ResetPasswordError
//import com.judahben149.tala.domain.models.authentication.SignInWithEmailAndPasswordError
//import com.judahben149.tala.domain.models.authentication.SocialMediaError
//import dev.gitlive.firebase.auth.AuthCredential
//import com.judahben149.tala.domain.models.common.Result
//
//interface AuthenticationRepository {
//    suspend fun logOut(): Result<Boolean, LogOutError>
//    suspend fun signUp(
//        email: String,
//        password: String,
//    ): Result<String, CreateUserWithEmailAndPasswordError>
//
//    suspend fun signInWithEmailAndPassword(
//        email: String,
//        password: String,
//    ): Result<String, SignInWithEmailAndPasswordError>
//
//    suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError>
//    suspend fun signInWithCredential(
//        authCredential: AuthCredential,
//        providerType: ProviderType
//    ): Result<FirebaseUserInfoDomainModel, SocialMediaError>
//
//    suspend fun linkInWithCredential(
//        authCredential: AuthCredential
//    ): Result<FirebaseUserInfoDomainModel, SocialMediaError>
//
//    suspend fun isFirstSignIn(uid: String): Boolean
//    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
//}
