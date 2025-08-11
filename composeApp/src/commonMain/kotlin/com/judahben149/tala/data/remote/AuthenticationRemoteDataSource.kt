package com.judahben149.tala.data.remote

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
//interface AuthenticationRemoteDataSource {
//    suspend fun authenticationSignOut(): Result<Boolean, LogOutError>
//    suspend fun signInWithCredentials(credential: AuthCredential, providerType: ProviderType)
//        : Result<FirebaseUserInfoDomainModel, SocialMediaError>
//
//    suspend fun linkWithCredential(credential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaError>
//    suspend fun createUserWithEmailAndPassword(
//        email: String,
//        password: String
//    ): Result<String, CreateUserWithEmailAndPasswordError>
//
//    suspend fun signInWithEmailAndPassword(
//        email: String,
//        password: String
//    ): Result<String, SignInWithEmailAndPasswordError>
//
//    suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError>
//    suspend fun getUserUid(): String?
//    suspend fun isFirstSignIn(uid: String): Boolean
//    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
//}