package dev.bogwalk.models

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: Int,
    val name: String,
    val size: Int
)

@Serializable
data class Question(
    val id: Int,
    val content: String,
    val optionalAnswer1: String,
    val optionalAnswer2: String,
    val optionalAnswer3: String,
    val optionalAnswer4: String,
    val expectedAnswer: String
)