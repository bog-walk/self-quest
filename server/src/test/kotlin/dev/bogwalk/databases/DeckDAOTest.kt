package dev.bogwalk.databases

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
        }
        val lastAdded = testDAO.addNewDeck("Last $deckName")
        val decks = testDAO.allDecks()

        assertEquals(4, decks.size)
        assertEquals("$deckName 1", decks.first().name)
        assertTrue { "Last $deckName" == lastAdded?.name &&
                lastAdded.name == decks.last().name
        }
        assertTrue { decks.all { it.size == 0 } }
    }

    @Test
    fun `selection by id returns existing Deck`() = runBlocking {
        val newDeck = testDAO.addNewDeck("Test")

        assertNotNull(newDeck)
        val result = testDAO.deck(newDeck.id)

        assertEquals(newDeck, result)
    }

    @Test
    fun `selection by id returns null for non-existent id`(): Unit = runBlocking {
        assertNull(testDAO.deck(1))

        testDAO.addNewDeck("Test")

        assertNull(testDAO.deck(99999))
    }

    @Test
    fun `edit by id correctly edits Deck if it exists`() = runBlocking {
        assertFalse { testDAO.editDeck(1, "Test", 0) }

        val original = testDAO.addNewDeck("Test")

        val newName = "Real"
        assertTrue { testDAO.editDeck(1, newName, 0) }

        val updated = testDAO.deck(1)

        assertNotEquals(original, updated)
        assertEquals(newName, updated?.name)
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

        for (i in 1..3) {
            testDAO.addNewQuestion(
                deck.id, "Fake question", "A", "B", "C", "D", "A"
            )
        }
        val firstQ = testDAO.question(deck.id)
        assertNotNull(firstQ)

        assertTrue { testDAO.deleteDeck(deck.id) }
        assertTrue { testDAO.allQuestions(deck.id).isEmpty() }
        for (i in 1..3) {
            assertNull(testDAO.question(i))
        }
    }
}