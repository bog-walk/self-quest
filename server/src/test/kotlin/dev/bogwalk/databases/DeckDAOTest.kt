package dev.bogwalk.databases

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.*

// Unless run separately, every test but the first throws a SQLException
// when attempting to find a Connection as first Pool will have closed...
// Is the setUp and tearDown structure incorrect?
internal class DeckDAOTest {
    private lateinit var dbFactory: DatabaseFactoryTestImpl
    private lateinit var testDAO: DAOFacadeImpl

    @BeforeTest
    fun setUp() {
        dbFactory = DatabaseFactoryTestImpl()
        dbFactory.connect()
        testDAO = DAOFacadeImpl(dbFactory)
    }

    @AfterTest
    fun tearDown() {
        dbFactory.close()
    }

    @Test
    fun `test Deck insertion and selection of all instances`() = runBlocking {
        assertTrue { testDAO.allDecks().isEmpty() }

        val deckName = "Test Deck"
        for (i in 1..3) {
            testDAO.addNewDeck("$deckName $i")
            delay(100)
        }
        val lastAdded = testDAO.addNewDeck("Last $deckName")
        val decks = testDAO.allDecks()

        assertEquals(4, decks.size)
        assertEquals("$deckName 1", decks.last().name)
        assertTrue { "Last $deckName" == lastAdded?.name &&
                lastAdded.name == decks.first().name
        }
        assertTrue { decks.all { it.size == 0 } }
    }

    @Test
    fun `selection by id returns existing empty Deck`() = runBlocking {
        val newDeck = testDAO.addNewDeck("Test")

        assertNotNull(newDeck)
        val result = testDAO.deck(newDeck.id)

        assertEquals(newDeck, result)
        assertEquals(0, result?.size)
    }

    @Test
    fun `selection by id returns existing Deck`() = runBlocking {
        val newDeck = testDAO.addNewDeck("Test")

        assertNotNull(newDeck)
        assertEquals(0, newDeck.size)
        repeat(10) {
            testDAO.addNewQuestion(
                newDeck.id, "Fake question", "A", "B", "C", "D", "A"
            )
        }
        val result = testDAO.deck(newDeck.id)

        assertEquals(newDeck.id, result?.id)
        assertEquals(newDeck.name, result?.name)
        assertEquals(10, result?.size)
    }

    @Test
    fun `selection by id returns null for non-existent id`(): Unit = runBlocking {
        assertNull(testDAO.deck(1))

        testDAO.addNewDeck("Test")

        assertNull(testDAO.deck(99999))
    }

    @Test
    fun `edit by id correctly edits Deck if it exists`() = runBlocking {
        assertFalse { testDAO.editDeck(1, "Test") }

        val original = testDAO.addNewDeck("Test")

        assertNotNull(original)
        val newName = "Real"
        assertTrue { testDAO.editDeck(original.id, newName) }

        val updated = testDAO.deck(original.id)

        assertNotEquals(original, updated)
        assertEquals(newName, updated?.name)
    }

    @Test
    fun `editing a Deck does not affect its Questions`() = runBlocking {
        val original = testDAO.addNewDeck("Test")
        assertNotNull(original)
        assertEquals(0, original.size)

        for (i in 1..3) {
            testDAO.addNewQuestion(
                original.id, "Fake question", "A", "B", "C", "D", "A"
            )
        }

        val deckQuestions = testDAO.allQuestions(original.id)
        val newName = "Real"
        assertTrue { testDAO.editDeck(original.id, newName) }

        val updated = testDAO.deck(original.id)
        assertNotNull(updated)
        assertEquals(newName, updated.name)
        assertEquals(3, updated.size)
        assertContentEquals(deckQuestions, testDAO.allQuestions(updated.id))
    }

    @Test
    fun `delete by id correctly deletes Deck if it exists`() = runBlocking {
        assertFalse { testDAO.deleteDeck(1) }

        val deckName = "Test Deck"
        for (i in 1..3) {
            testDAO.addNewDeck("$deckName $i")
        }
        val decks = testDAO.allDecks()
        assertEquals(3, decks.size)
        val deckToDelete = decks.first()

        assertTrue { testDAO.deleteDeck(deckToDelete.id) }

        val decksUpdated = testDAO.allDecks()
        assertEquals(2, decksUpdated.size)
        assertFalse { deckToDelete in decksUpdated }
    }

    @Test
    fun `deleting a Deck deletes all its Questions`() = runBlocking {
        val deck = testDAO.addNewDeck("Test")
        assertNotNull(deck)

        repeat (3) {
            assertNotNull(testDAO.addNewQuestion(
                deck.id, "Fake question", "A", "B", "C", "D", "A"
            ))
        }
        assertEquals(3, testDAO.deck(deck.id)?.size)

        assertTrue { testDAO.deleteDeck(deck.id) }
        assertTrue { testDAO.allQuestions(deck.id).isEmpty() }
        for (i in 1..3) {
            assertNull(testDAO.question(i))
        }
    }
}