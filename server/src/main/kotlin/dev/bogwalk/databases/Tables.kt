package dev.bogwalk.databases

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaDefinition {
    fun createSchema() {
        transaction {
            SchemaUtils.create(Decks)
            SchemaUtils.create(Questions)
        }
    }
}

object Decks : IntIdTable() {
    val name = varchar("name", 64)
    val qCount = integer("q_count")
}

object Questions : IntIdTable() {
    val deckId = reference("deck_id", Decks, onDelete = ReferenceOption.CASCADE)
    val content = varchar("content", 512)
    val option1 = varchar("option_1", 128)
    val option2 = varchar("option_2",128)
    val option3 = varchar("option_3", 128)
    val option4 = varchar("option_4", 128)
    val correct = varchar("correct", 128)
}