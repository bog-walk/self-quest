package dev.bogwalk.databases

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaDefinition {
    fun createSchema() {
        transaction {
            SchemaUtils.create(Decks)
            SchemaUtils.create(Questions)
            SchemaUtils.create(Answers)
        }
    }
}

object Decks : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 64)
    val qCount = integer("q_count")

    override val primaryKey = PrimaryKey(id)
}

object Questions : Table() {
    val id = integer("id").autoIncrement()
    val deckId = (integer("deck_id") references Decks.id)
    val content = varchar("content", 256)
    val option1 = (integer("option_1_id") references Answers.id)
    val option2 = (integer("option_2_id") references Answers.id)
    val option3 = (integer("option_3_id") references Answers.id)
    val option4 = (integer("option_4_id") references Answers.id)
    val correct = (integer("correct_id") references Answers.id)

    override val primaryKey = PrimaryKey(id)
}

object Answers : Table() {
    val id = integer("id")
    val content = varchar("content", 32)

    override val primaryKey = PrimaryKey(id)
}