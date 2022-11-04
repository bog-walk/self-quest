package dev.bogwalk.client

import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review

interface Client {
    fun cleanUp()
    suspend fun loadSavedDecks()
    fun showDeck(deck: Deck)
    fun addNewDeck(name: String)
    fun editDeck(updated: Deck)
    fun addNewQuestion(newQuestion: Question)
    fun editQuestion(updated: Question)
    fun updateReview(updated: Review)
    fun confirmDelete()
}