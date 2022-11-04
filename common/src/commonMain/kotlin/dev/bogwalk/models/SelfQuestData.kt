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
    val optionalAnswers: List<String>,
    val expectedAnswer: String,
    val review: Review?
)

@Serializable
data class Review(
    val content: String,
    val references: List<Pair<String, String>>
)