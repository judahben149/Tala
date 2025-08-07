package com.judahben149.tala.domain.model

import kotlinx.datetime.Instant

data class TestEntity(
    val id: Int = 0,
    val name: String,
    val description: String,
    val createdAt: Instant
)
