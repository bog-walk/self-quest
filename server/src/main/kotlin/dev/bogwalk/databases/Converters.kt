package dev.bogwalk.databases

import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
import org.jetbrains.exposed.sql.ResultRow

object Converters {
    fun rowToDeck(row: ResultRow) = Deck(
        id = row[Decks.id].value,
        name = row[Decks.name],
        size = row[Decks.qCount]
    )

    fun rowToQuestion(row: ResultRow, refRows: List<ResultRow>) = Question(
        id = row[Questions.id].value,
        content = row[Questions.content],
        optionalAnswer1 = row[Questions.option1],
        optionalAnswer2 = row[Questions.option2],
        optionalAnswer3 = row[Questions.option3],
        optionalAnswer4 = row[Questions.option4],
        expectedAnswer = row[Questions.correct],
        review = rowToReview(row[Questions.review], refRows)
    )

    fun rowToReview(content: String, rows: List<ResultRow>): Review? {
        return if (content.isEmpty() && rows.isEmpty()) {
            null
        } else {
            Review(
                content = content,
                references = rows.map { it[References.name] to it[References.uri] }
            )
        }
    }
}