package com.judahben149.tala.util

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun DatabaseReference.removeValue(): Unit = suspendCancellableCoroutine { continuation ->
    this.removeValue().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to remove value"))
        }
    }
}

suspend fun FirebaseUser.delete(): Unit = suspendCancellableCoroutine { continuation ->
    this.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to delete user"))
        }
    }
}