package dev.bogwalk.databases

import dev.bogwalk.models.Deck
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.count

internal object Converters {
    fun rowToDeck(row: ResultRow) = Deck(
        id = row[Decks.id].value,
        name = row[Decks.name],
        size = row.getOrNull(Questions.id.count())?.toInt() ?: 0
    )

    fun rowToQuestion(row: ResultRow, refRows: List<ResultRow>) = Question(
        id = row[Questions.id].value,
        content = row[Questions.content],
        optionalAnswers = listOf(
            row[Questions.option1], row[Questions.option2], row[Questions.option3], row[Questions.option4]
        ),
        expectedAnswer = row[Questions.correct],
        review = rowToReview(row[Questions.review], refRows)
    )

    private fun rowToReview(content: String, rows: List<ResultRow>): Review? {
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