package com.judahben149.tala.data.repository

//import com.judahben149.tala.data.remote.AuthenticationRemoteDataSource
//import com.judahben149.tala.domain.mappers.mapError
//import com.judahben149.tala.domain.mappers.mapSuccess
//import com.judahben149.tala.domain.models.authentication.CreateUserWithEmailAndPasswordError
//import com.judahben149.tala.domain.models.authentication.FirebaseUserInfoDomainModel
//import com.judahben149.tala.domain.models.authentication.LogOutError
//import com.judahben149.tala.domain.models.authentication.ProviderType
//import com.judahben149.tala.domain.models.authentication.ResetPasswordError
//import com.judahben149.tala.domain.models.authentication.SignInWithEmailAndPasswordError
//import com.judahben149.tala.domain.models.authentication.SocialMediaError
//import com.judahben149.tala.domain.repository.AuthenticationRepository
//import com.judahben149.tala.domain.models.common.Result
//import dev.gitlive.firebase.auth.AuthCredential
//import kotlin.coroutines.cancellation.CancellationException
//
//class AuthenticationRepositoryImpl(
//    private val remoteDataSource: AuthenticationRemoteDataSource
//) : AuthenticationRepository {
//    override suspend fun logOut(): Result<Boolean, LogOutError> {
//        return try {
//            when (val result = remoteDataSource.authenticationSignOut()) {
//                is Result.Success, Result.Loading -> {
//                    Result.Success(true)
//                }
//
//                is Result.Failure -> {
//                    Result.Failure(result.error)
//                }
//            }
//        } catch (exception: Exception) {
//            Result.Failure(LogOutError.UnknownError(exception.message ?: ""))
//        }
//    }
//
//    override suspend fun signUp(
//        email: String,
//        password: String,
//    ): Result<String, CreateUserWithEmailAndPasswordError> {
//        return try {
//            remoteDataSource.createUserWithEmailAndPassword(email, password)
//        } catch (e: CancellationException) {
//            throw e
//        } catch (e: Exception) {
//            Result.Failure(
//                CreateUserWithEmailAndPasswordError.Other(
//                    message = e.message ?: "Error while creating user"
//                )
//            )
//        }
//    }
//
//    override suspend fun signInWithEmailAndPassword(
//        email: String,
//        password: String,
//    ): Result<String, SignInWithEmailAndPasswordError> {
//        return remoteDataSource.signInWithEmailAndPassword(email, password)
//            .mapSuccess { it }
//            .mapError { error ->
//                return@mapError error
//            }
//    }
//
//    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
//        remoteDataSource.resetPassword(email)
//
//    override suspend fun signInWithCredential(
//        authCredential: AuthCredential,
//        providerType: ProviderType
//    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> {
//        return remoteDataSource.signInWithCredentials(authCredential, providerType)
//    }
//
//    override suspend fun linkInWithCredential(
//        authCredential: AuthCredential
//    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> {
//        return remoteDataSource.linkWithCredential(credential = authCredential)
//    }
//
//    override suspend fun isFirstSignIn(uid: String): Boolean {
//        return remoteDataSource.isFirstSignIn(uid)
//    }
//
//    override suspend fun fetchFirebaseUserInfo() =
//        remoteDataSource.fetchFirebaseUserInfo()
//}
