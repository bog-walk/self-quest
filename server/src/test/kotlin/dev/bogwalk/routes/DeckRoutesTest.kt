package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacadeImpl
import dev.bogwalk.databases.DatabaseFactoryTestImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.*

internal class DeckRoutesTest {
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
    fun `GET returns all stored Deck instances`() = runBlocking {
        assertTrue { testDAO.allDecks().isEmpty() }

        val deckName = "Test Deck"
        testDAO.addNewDeck(deckName)
        val decks = testDAO.allDecks()

        assertEquals(1, decks.size)
        assertEquals(deckName, decks.first().name)
    }
}