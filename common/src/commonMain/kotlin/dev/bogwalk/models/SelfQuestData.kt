package dev.bogwalk.models

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: Int,
    val name: String
)

@Serializable
data class Question(
    val id: Int,
    val content: String,
    val optionalAnswers: List<Answer>,
    val expectedAnswer: Answer
)

@Serializable
data class Answer(
    val id: Int,
    val content: String
)