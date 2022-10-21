package dev.bogwalk.databases

import kotlinx.coroutines.runBlocking
import kotlin.test.*

// Unless run separately, every test but the first throws a SQLException
// when attempting to find a Connection as first Pool will have closed...
// Is the setUp and tearDown structure incorrect?
internal class QuestionDAOTest {
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
    fun `test Question insertion and selection of all instances`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)

        assertTrue { testDAO.allQuestions(deck.id).isEmpty() }

        val addedQ = testDAO.addNewQuestion(
            deck.id, "Fake question", "A", "B", "C", "D", "A"
        )
        val questions = testDAO.allQuestions(deck.id)

        assertEquals(1, questions.size)
        assertContains(questions, addedQ)
    }

    @Test
    fun `selection by id returns existing Question`() = runBlocking {
        testDAO.addNewDeck("Test Deck")

        val addedQ = testDAO.addNewQuestion(
            1, "Fake question", "A", "B", "C", "D", "A"
        )

        val result = testDAO.question(1)

        assertEquals(addedQ?.content, result?.content)
    }

    @Test
    fun `selection by id returns null for non-existent id`(): Unit = runBlocking {
        assertNull(testDAO.question(1))

        testDAO.addNewDeck("Test Deck")
        testDAO.addNewQuestion(
            1, "Fake question", "A", "B", "C", "D", "A"
        )

        assertNull(testDAO.question(9999))
    }

    @Test
    fun `edit by id correctly edits Question if it exists`() = runBlocking {
        testDAO.addNewDeck("Test Deck")
        assertFalse { testDAO.editQuestion(
            1, "Fake question", "A", "B", "C", "D", "A"
        ) }

        val original = testDAO.addNewQuestion(
            1, "Fake question", "A", "B", "C", "D", "A"
        )

        val newAnswer = "C"
        assertTrue { testDAO.editQuestion(
            1, "Fake question", "A", "B", "C", "D", newAnswer
        ) }

        val updated = testDAO.question(1)

        assertNotEquals(original, updated)
        assertEquals(newAnswer, updated?.expectedAnswer)
    }

    @Test
    fun `delete by id correctly deletes Question if it exists`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)
        assertFalse { testDAO.deleteQuestion(1) }

        for (i in 1..3) {
            testDAO.addNewQuestion(
                1, "Fake question", "A", "B", "C", "D", "A"
            )
        }
        val questions = testDAO.allQuestions(deck.id)
        assertEquals(3, questions.size)
        val questionToDelete = questions.first()

        assertTrue { testDAO.deleteQuestion(questionToDelete.id) }

        val questionsUpdated = testDAO.allQuestions(deck.id)
        assertEquals(2, questionsUpdated.size)
        assertFalse { questionToDelete in questionsUpdated }
    }
}