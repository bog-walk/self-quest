package dev.bogwalk.databases

import dev.bogwalk.databases.DatabaseFactory.dbQuery
import dev.bogwalk.models.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl : DAOFacade {
    override suspend fun allDecks(): List<Deck> = dbQuery {
        Decks.selectAll().map(::resultRowToDeck)
    }

    override suspend fun deck(id: Int): Deck? = dbQuery {
        Decks.select { Decks.id eq id }.map(::resultRowToDeck).singleOrNull()
    }

    override suspend fun addNewDeck(name: String): Deck? = dbQuery {
        val insertStatement = Decks.insert {
            it[Decks.name] = name
            it[qCount] = 0
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToDeck)
    }

    override suspend fun editDeck(id: Int, name: String): Boolean = dbQuery {
        Decks.update({ Decks.id eq id }) {
            it[Decks.name] = name
        } > 0
    }

    override suspend fun deleteDeck(id: Int): Boolean = dbQuery {
        Decks.deleteWhere { Decks.id eq id } > 0
    }

    override suspend fun allQuestions(deckId: Int): List<Question> = dbQuery {
        Questions
            .slice(Questions.id, Questions.content)
            .select { Questions.deckId eq deckId }
            .map { question(it[Questions.id])!! }
    }

    override suspend fun question(id: Int): Question? = dbQuery {
        val row = Questions.select { Questions.id eq id }.singleOrNull() ?: return@dbQuery null
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
    ): Question? = dbQuery {
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
        val result = insertStatement.resultedValues?.singleOrNull() ?: return@dbQuery null
        Question(
            result[Questions.id], result[Questions.content], optionalAnswers, expectedAnswer
        )
    }

    override suspend fun editQuestion(
        id: Int, content: String, options: List<String>, correct: String
    ): Boolean = dbQuery {
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

    override suspend fun deleteQuestion(id: Int): Boolean = dbQuery {
        Questions.deleteWhere { Questions.id eq id } > 0
    }

    override suspend fun answer(id: Int): Answer? = dbQuery {
        Answers.select { Answers.id eq id }.map(::resultRowToAnswer).singleOrNull()
    }

    override suspend fun addNewAnswer(content: String): Answer? = dbQuery {
        val insertStatement = Answers.insert {
            it[Answers.content] = content
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToAnswer)
    }

    override suspend fun editAnswer(id: Int, content: String): Boolean = dbQuery {
        Answers.update({ Answers.id eq id }) {
            it[Answers.content] = content
        } > 0
    }

    override suspend fun deleteAnswer(id: Int): Boolean = dbQuery {
        Answers.deleteWhere { Answers.id eq id } > 0
    }

    private fun resultRowToDeck(row: ResultRow) = Deck(
        id = row[Decks.id],
        name = row[Decks.name]
    )

    private fun resultRowToAnswer(row: ResultRow) = Answer(
        id = row[Answers.id],
        content = row[Answers.content]
    )
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if (allDecks().isEmpty()) {
            val newDeck = addNewDeck("Equine Vet")
            newDeck?.let {
                addNewQuestion(
                    it.id, q1.content, listOf(a1, a2, a3, a4).map(Answer::content), a3.content
                )
                addNewQuestion(
                    it.id, q2.content, listOf(a5, a6, a7, a8).map(Answer::content), a5.content
                )
                addNewQuestion(
                    it.id, q3.content, listOf(a3, a9, a4, a1).map(Answer::content), a3.content
                )
            }
        }
    }
}