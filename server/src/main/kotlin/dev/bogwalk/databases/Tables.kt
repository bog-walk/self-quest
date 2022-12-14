package dev.bogwalk.databases

import dev.bogwalk.models.DataLength
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaDefinition {
    fun createSchema() {
        transaction {
            SchemaUtils.create(Decks)
            SchemaUtils.create(Questions)
            SchemaUtils.create(References)
        }
    }
}

object Decks : IntIdTable() {
    val name = varchar("name", DataLength.DeckName)
    val updated = datetime("last_updated")
}

object Questions : IntIdTable() {
    val deckId = reference("deck_id", Decks, onDelete = ReferenceOption.CASCADE)
    val updated = datetime("last_updated")
    val content = varchar("content", DataLength.QuestionContent)
    val option1 = varchar("option_1", DataLength.QuestionOption)
    val option2 = varchar("option_2",DataLength.QuestionOption)
    val option3 = varchar("option_3", DataLength.QuestionOption)
    val option4 = varchar("option_4", DataLength.QuestionOption)
    val correct = varchar("correct", DataLength.QuestionOption)
    // ERROR -> org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: NULL not allowed
    //val review = varchar("review", DataLength.ReviewContent).nullable()
    val review = varchar("review", DataLength.ReviewContent)
}

object References : Table() {
    val questionId = reference("question_id", Questions.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", DataLength.ReviewRefName)
    val uri = varchar("uri", DataLength.ReviewRefUri)
}