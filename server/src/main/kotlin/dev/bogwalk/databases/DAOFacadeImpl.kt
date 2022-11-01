package dev.bogwalk.databases

import dev.bogwalk.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class DAOFacadeImpl(private val db: DatabaseFactory) : DAOFacade {
    override suspend fun allDecks(): List<Deck> = db.query {
        (Decks leftJoin Questions)
            .slice(Decks.id, Decks.name, Questions.id.count())
            .selectAll()
            .groupBy(Decks.id)
            .orderBy(Decks.updated to SortOrder.DESC)
            .map(Converters::rowToDeck)
    }

    // is this ever used?
    override suspend fun deck(id: Int): Deck? = db.query {
        (Decks leftJoin Questions)
            .slice(Decks.id, Decks.name, Questions.id.count())
            .select { Decks.id eq id }
            .groupBy(Decks.id)
            .map(Converters::rowToDeck)
            .singleOrNull()
    }

    override suspend fun addNewDeck(name: String): Deck? = db.query {
        val insertStatement = Decks.insert {
            it[Decks.name] = name
            it[updated] = LocalDateTime.now()
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Converters::rowToDeck)
    }

    // should adding questions to an empty deck cause the updated datetime to alter?
    override suspend fun editDeck(id: Int, name: String): Boolean = db.query {
        Decks.update({ Decks.id eq id }) {
            it[Decks.name] = name
            it[updated] = LocalDateTime.now()
        } > 0
    }

    override suspend fun deleteDeck(id: Int): Boolean = db.query {
        Decks.deleteWhere { Decks.id eq id } > 0
    }

    // enforce pagination using limit?
    override suspend fun allQuestions(deckId: Int): List<Question> = db.query {
        Questions.slice(
            Questions.id, Questions.content, Questions.option1, Questions.option2,
            Questions.option3, Questions.option4, Questions.correct, Questions.review
        ).select { Questions.deckId eq deckId }
            .orderBy(Questions.updated to SortOrder.DESC)
            .map { resultRow ->
                val refs = References.slice(References.name, References.uri)
                    .select { References.questionId eq resultRow[Questions.id] }.toList()
                Converters.rowToQuestion(resultRow, refs)
        }
    }

    override suspend fun question(id: Int): Question? = db.query {
        Questions.slice(
            Questions.id, Questions.content, Questions.option1, Questions.option2, Questions.option3,
            Questions.option4, Questions.correct, Questions.review
        ).select { Questions.id eq id }
            .map { resultRow ->
                val refs = References.slice(References.name, References.uri)
                    .select { References.questionId eq resultRow[Questions.id] }.toList()
                Converters.rowToQuestion(resultRow, refs)
        }.singleOrNull()
    }

    override suspend fun addNewQuestion(
        deckId: Int, content: String,
        option1: String, option2: String, option3: String, option4: String,
        correct: String
    ): Question? = db.query {
        val insertStatement = Questions.insert {
            it[Questions.deckId] = deckId
            it[updated] = LocalDateTime.now()
            it[Questions.content] = content
            it[Questions.option1] = option1
            it[Questions.option2] = option2
            it[Questions.option3] = option3
            it[Questions.option4] = option4
            it[Questions.correct] = correct
            it[review] = ""
        }
        insertStatement.resultedValues?.singleOrNull()?.let {
            Converters.rowToQuestion(it, emptyList())
        }
    }

    // should updating a question not later the updated date for parent deck?
    override suspend fun editQuestion(
        id: Int, content: String,
        option1: String, option2: String, option3: String, option4: String,
        correct: String
    ): Boolean = db.query {
        Questions.update({ Questions.id eq id }) {
            it[Questions.content] = content
            it[updated] = LocalDateTime.now()
            it[Questions.option1] = option1
            it[Questions.option2] = option2
            it[Questions.option3] = option3
            it[Questions.option4] = option4
            it[Questions.correct] = correct
        } > 0
    }

    override suspend fun editReview(
        questionId: Int, content: String, references: List<Pair<String, String>>
    ): Boolean = db.query {
        val qUpdated = Questions.update({ Questions.id eq questionId }) {
            it[updated] = LocalDateTime.now()
            it[review] = content
        } > 0
        if (qUpdated && references.isNotEmpty()) {
            References.deleteWhere { References.questionId eq questionId }
            References.batchInsert(references, shouldReturnGeneratedValues = false) {
                this[References.questionId] = questionId
                this[References.name] = it.first
                this[References.uri] = it.second
            }
        }
        qUpdated
    }

    override suspend fun deleteReview(questionId: Int): Boolean = db.query {
        Questions.update({ Questions.id eq questionId }) {
            it[updated] = LocalDateTime.now()
            it[review] = ""
        } > 0 && References.deleteWhere { References.questionId eq questionId } > 0
    }

    override suspend fun deleteQuestion(id: Int): Boolean = db.query {
        Questions.deleteWhere { Questions.id eq id } > 0
    }
}