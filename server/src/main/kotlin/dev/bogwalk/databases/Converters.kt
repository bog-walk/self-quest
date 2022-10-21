package dev.bogwalk.databases

import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import org.jetbrains.exposed.sql.ResultRow

object Converters {
    fun rowToDeck(row: ResultRow) = Deck(
        id = row[Decks.id].value,
        name = row[Decks.name],
        size = row[Decks.qCount]
    )

    fun rowToQuestion(row: ResultRow) = Question(
        id = row[Questions.id].value,
        content = row[Questions.content],
        optionalAnswer1 = row[Questions.option1],
        optionalAnswer2 = row[Questions.option2],
        optionalAnswer3 = row[Questions.option3],
        optionalAnswer4 = row[Questions.option4],
        expectedAnswer = row[Questions.correct]
    )
}