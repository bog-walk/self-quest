package dev.bogwalk.databases

import dev.bogwalk.models.Answer
import dev.bogwalk.models.Deck
import org.jetbrains.exposed.sql.ResultRow

object Converters {
    fun rowToDeck(row: ResultRow) = Deck(
        id = row[Decks.id],
        name = row[Decks.name]
    )

    fun rowToAnswer(row: ResultRow) = Answer(
        id = row[Answers.id],
        content = row[Answers.content]
    )
}