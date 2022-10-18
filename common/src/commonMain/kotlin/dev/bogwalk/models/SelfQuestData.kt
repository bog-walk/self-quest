package dev.bogwalk.models

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: String,
    val name: String,
    val questions: MutableList<Question>
) {
    val size = questions.size
}

@Serializable
data class Question(
    val id: String,
    val content: String,
    val optionalAnswers: List<String>,
    val expectedAnswer: String
)

@Serializable
data class Answer(
    val id: String,
    val content: String,
    val associations: MutableList<String>
)