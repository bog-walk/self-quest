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

    override suspend fun editDeck(id: Int, name: String): Boolean = db.query {
        Decks.update({ Decks.id eq id }) {
            it[Decks.name] = name
        } > 0
    }

    override suspend fun deleteDeck(id: Int): Boolean = db.query {
        Decks.deleteWhere { Decks.id eq id } > 0
    }

    override suspend fun allQuestions(deckId: Int): List<Question> = db.query {
        Questions
            .slice(Questions.id, Questions.content)
            .select { Questions.deckId eq deckId }
            .map { question(it[Questions.id])!! }
    }

    override suspend fun question(id: Int): Question? = db.query {
        val row = Questions.select { Questions.id eq id }.singleOrNull() ?: return@query null
        val correct = answer(row[Questions.correct])!!
        val options = listOf(
            row[Questions.option1], row[Questions.option2],
            row[Questions.option3], row[Questions.option4]
        ).map {
            if (it == row[Questions.correct]) correct else answer(it)!!
        }
        Question(row[Questions.id], row[Questions.content], options, correct)
    }

    override suspend fun addNewQuestion(
        deckId: Int, content: String, options: List<String>, correct: String
    ): Question? = db.query {
        val expectedAnswer = addNewAnswer(correct)!!
        val optionalAnswers = options.map { addNewAnswer(it)!! }
        val insertStatement = Questions.insert {
            it[Questions.deckId] = deckId
            it[Questions.content] = content
            it[option1] = optionalAnswers[0].id
            it[option2] = optionalAnswers[1].id
            it[option3] = optionalAnswers[2].id
            it[option4] = optionalAnswers[3].id
            it[Questions.correct] = expectedAnswer.id
        }
        val result = insertStatement.resultedValues?.singleOrNull() ?: return@query null
        Question(
            result[Questions.id], result[Questions.content], optionalAnswers, expectedAnswer
        )
    }

    override suspend fun editQuestion(
        id: Int, content: String, options: List<String>, correct: String
    ): Boolean = db.query {
        val expectedAnswer = addNewAnswer(correct)!!
        val optionalAnswers = options.map { addNewAnswer(it)!! }
        Questions.update({ Questions.id eq id }) {
            it[Questions.content] = content
            it[option1] = optionalAnswers[0].id
            it[option2] = optionalAnswers[1].id
            it[option3] = optionalAnswers[2].id
            it[option4] = optionalAnswers[3].id
            it[Questions.correct] = expectedAnswer.id
        } > 0
    }

    override suspend fun deleteQuestion(id: Int): Boolean = db.query {
        Questions.deleteWhere { Questions.id eq id } > 0
    }

    override suspend fun answer(id: Int): Answer? = db.query {
        Answers.select { Answers.id eq id }.map(Converters::rowToAnswer).singleOrNull()
    }

    override suspend fun addNewAnswer(content: String): Answer? = db.query {
        val insertStatement = Answers.insert {
            it[Answers.content] = content
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Converters::rowToAnswer)
    }

    override suspend fun editAnswer(id: Int, content: String): Boolean = db.query {
        Answers.update({ Answers.id eq id }) {
            it[Answers.content] = content
        } > 0
    }

    override suspend fun deleteAnswer(id: Int): Boolean = db.query {
        Answers.deleteWhere { Answers.id eq id } > 0
    }
}