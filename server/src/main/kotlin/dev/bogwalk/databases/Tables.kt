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
    val name = varchar("name", DataLength.DECK_NAME)
    val updated = datetime("last_updated")
}

object Questions : IntIdTable() {
    val deckId = reference("deck_id", Decks, onDelete = ReferenceOption.CASCADE)
    val updated = datetime("last_updated")
    val content = varchar("content", DataLength.QUESTION_CONTENT)
    val option1 = varchar("option_1", DataLength.QUESTION_OPTION)
    val option2 = varchar("option_2",DataLength.QUESTION_OPTION)
    val option3 = varchar("option_3", DataLength.QUESTION_OPTION)
    val option4 = varchar("option_4", DataLength.QUESTION_OPTION)
    val correct = varchar("correct", DataLength.QUESTION_OPTION)
    // ERROR -> org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: NULL not allowed
    //val review = varchar("review", DataLength.ReviewContent).nullable()
    val review = varchar("review", DataLength.REVIEW_CONTENT)
}

object References : Table() {
    val questionId = reference("question_id", Questions.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", DataLength.REVIEW_REF_NAME)
    val uri = varchar("uri", DataLength.REVIEW_REF_URI)
}