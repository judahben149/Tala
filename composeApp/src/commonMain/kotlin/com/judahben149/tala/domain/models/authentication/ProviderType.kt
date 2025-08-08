package com.judahben149.tala.domain.models.authentication

enum class ProviderType(val className: String) {
    GOOGLE("GoogleAuthProvider"),
    APPLE("AppleAuthProvider")
}