package dev.bogwalk.databases

import dev.bogwalk.models.Answer
import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question

interface DAOFacade {
    suspend fun allDecks(): List<Deck>
    suspend fun deck(id: Int): Deck?
    suspend fun addNewDeck(name: String): Deck?
    suspend fun editDeck(id: Int, name: String): Boolean
    suspend fun deleteDeck(id: Int): Boolean

    suspend fun allQuestions(deckId: Int): List<Question>
    suspend fun question(id: Int): Question?
    suspend fun addNewQuestion(
        deckId: Int, content: String, options: List<String>, correct: String
    ): Question?
    suspend fun editQuestion(
        id: Int, content: String, options: List<String>, correct: String
    ): Boolean
    suspend fun deleteQuestion(id: Int): Boolean

    suspend fun answer(id: Int): Answer?
    suspend fun addNewAnswer(content: String): Answer?
    suspend fun editAnswer(id: Int, content: String): Boolean
    suspend fun deleteAnswer(id: Int): Boolean
}