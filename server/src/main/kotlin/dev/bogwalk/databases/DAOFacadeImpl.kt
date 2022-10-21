package dev.bogwalk.databases

import dev.bogwalk.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl(private val db: DatabaseFactory) : DAOFacade {
    override suspend fun allDecks(): List<Deck> = db.query {
        Decks.selectAll().map(Converters::rowToDeck)
    }

    override suspend fun deck(id: Int): Deck? = db.query {
        Decks.select { Decks.id eq id }.map(Converters::rowToDeck).singleOrNull()
    }

    override suspend fun addNewDeck(name: String): Deck? = db.query {
        val insertStatement = Decks.insert {
            it[Decks.name] = name
            it[qCount] = 0
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Converters::rowToDeck)
    }

    override suspend fun editDeck(
        id: Int, name: String, size: Int
    ): Boolean = db.query {
        Decks.update({ Decks.id eq id }) {
            it[Decks.name] = name
            it[qCount] = size
        } > 0
    }

    override suspend fun deleteDeck(id: Int): Boolean = db.query {
        Decks.deleteWhere { Decks.id eq id } > 0
    }

    // enforce pagination using limit?
    override suspend fun allQuestions(deckId: Int): List<Question> = db.query {
        Questions.slice(
            Questions.id, Questions.content, Questions.option1, Questions.option2, Questions.option3,
            Questions.option4 ,Questions.correct
        ).select { Questions.deckId eq deckId }.map(Converters::rowToQuestion)
    }

    override suspend fun question(id: Int): Question? = db.query {
        Questions.slice(
            Questions.id, Questions.content, Questions.option1, Questions.option2, Questions.option3,
            Questions.option4 ,Questions.correct
        ).select { Questions.id eq id }.map(Converters::rowToQuestion).singleOrNull()
    }

    override suspend fun addNewQuestion(
        deckId: Int, content: String,
        option1: String, option2: String, option3: String, option4: String,
        correct: String
    ): Question? = db.query {
        val insertStatement = Questions.insert {
            it[Questions.deckId] = deckId
            it[Questions.content] = content
            it[Questions.option1] = option1
            it[Questions.option2] = option2
            it[Questions.option3] = option3
            it[Questions.option4] = option4
            it[Questions.correct] = correct
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Converters::rowToQuestion)
    }

    override suspend fun editQuestion(
        id: Int, content: String,
        option1: String, option2: String, option3: String, option4: String,
        correct: String
    ): Boolean = db.query {
        Questions.update({ Questions.id eq id }) {
            it[Questions.content] = content
            it[Questions.option1] = option1
            it[Questions.option2] = option2
            it[Questions.option3] = option3
            it[Questions.option4] = option4
            it[Questions.correct] = correct
        } > 0
    }

    override suspend fun deleteQuestion(id: Int): Boolean = db.query {
        Questions.deleteWhere { Questions.id eq id } > 0
    }
}