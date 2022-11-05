package dev.bogwalk.databases

import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question

interface DAOFacade {
    suspend fun allDecks(): List<Deck>
    suspend fun deck(id: Int): Deck?
    suspend fun addNewDeck(name: String): Int
    suspend fun editDeck(id: Int, name: String): Boolean
    suspend fun deleteDeck(id: Int): Boolean
    suspend fun allQuestions(deckId: Int): List<Question>
    suspend fun question(id: Int): Question?
    suspend fun addNewQuestion(
        deckId: Int, content: String,
        option1: String, option2: String, option3: String, option4: String,
        correct: String
    ): Int
    suspend fun editQuestion(
        id: Int, content: String,
        option1: String, option2: String, option3: String, option4: String,
        correct: String
    ): Boolean
    suspend fun editReview(
        questionId: Int, content: String, references: List<Pair<String, String>>
    ): Boolean
    suspend fun deleteReview(questionId: Int): Boolean
    suspend fun deleteQuestion(id: Int): Boolean
}