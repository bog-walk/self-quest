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

        val name = "Fake question"
        val addedQ = testDAO.addNewQuestion(
            deck.id, "$name 1", "A", "B", "C", "D", "A"
        )
        assertNotNull(addedQ)

        for (i in 2..4) {
            assertNotNull(testDAO.addNewQuestion(
                deck.id, "$name $i", "A", "B", "C", "D", "A"
            ))
        }
        val questions = testDAO.allQuestions(deck.id)

        assertEquals(4, questions.size)
        assertEquals("$name 4", questions.first().content)
        assertEquals("$name 1", questions.last().content)
    }

    @Test
    fun `selection by id returns existing new Question`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)

        val addedQ = testDAO.addNewQuestion(
            deck.id, "Fake question", "A", "B", "C", "D", "A"
        )

        val result = testDAO.question(deck.id)

        assertEquals(addedQ?.content, result?.content)
        assertNull(result?.review)
    }

    @Test
    fun `selection by id returns null for non-existent id`() = runBlocking {
        assertNull(testDAO.question(1))

        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)
        testDAO.addNewQuestion(
            deck.id, "Fake question", "A", "B", "C", "D", "A"
        )

        assertNull(testDAO.question(9999))
    }

    @Test
    fun `edit by id correctly edits Question if it exists`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)

        assertFalse { testDAO.editQuestion(
            1, "Fake question", "A", "B", "C", "D", "A"
        ) }

        val original = testDAO.addNewQuestion(
            deck.id, "Fake question", "A", "B", "C", "D", "A"
        )
        assertNotNull(original)

        val newAnswer = "C"
        assertTrue { testDAO.editQuestion(
            original.id,  "Fake question", "A", "B", "C", "D", newAnswer
        ) }

        val updated = testDAO.question(original.id)

        assertNotEquals(original, updated)
        assertEquals(original.id, updated?.id)
        assertEquals(newAnswer, updated?.expectedAnswer)
    }

    @Test
    fun `editReview correctly edits Question Review if it exists`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)

        assertFalse { testDAO.editReview(1, "Explanation", emptyList()) }

        val original = testDAO.addNewQuestion(
            deck.id, "Fake question", "A", "B", "C", "D", "A"
        )
        assertNotNull(original)
        assertNull(original.review)

        assertTrue { testDAO.editReview(original.id,  "Explanation", emptyList()) }

        val updated = testDAO.question(original.id)

        assertNotEquals(original, updated)
        assertEquals(original.id, updated?.id)
        assertEquals(original.content, updated?.content)

        val firstReview = updated?.review
        assertNotNull(firstReview)

        assertTrue { testDAO.editReview(original.id,  "", listOf("A" to "ALink", "B" to "BLink")) }

        val secondReview = testDAO.question(original.id)?.review
        assertNotNull(secondReview)

        assertNotEquals(firstReview, secondReview)
        assertTrue { secondReview.content.isEmpty() }
        assertEquals(2, secondReview.references.size)
    }

    @Test
    fun `delete by id correctly deletes Question if it exists`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)
        assertFalse { testDAO.deleteQuestion(1) }

        repeat (3) {
            testDAO.addNewQuestion(
                deck.id, "Fake question", "A", "B", "C", "D", "A"
            )
        }
        val questions = testDAO.allQuestions(deck.id)
        assertEquals(3, questions.size)
        val questionToDelete = questions.last()

        assertTrue { testDAO.deleteQuestion(questionToDelete.id) }

        val questionsUpdated = testDAO.allQuestions(deck.id)
        assertEquals(2, questionsUpdated.size)
        assertFalse { questionToDelete in questionsUpdated }
    }

    @Test
    fun `deleteReview correctly deletes Review only if it exists`() = runBlocking {
        val deck = testDAO.addNewDeck("Test Deck")
        assertNotNull(deck)
        assertFalse { testDAO.deleteReview(1) }

        val addedQ = testDAO.addNewQuestion(
            deck.id, "Fake question", "A", "B", "C", "D", "A"
        )
        assertNotNull(addedQ)
        assertTrue {
            testDAO.editReview(addedQ.id, "Explanation", listOf("A" to "ALink", "B" to "BLink"))
        }

        val original = testDAO.question(addedQ.id)
        assertEquals(2, original?.review?.references?.size)
        assertTrue { testDAO.deleteReview(addedQ.id) }

        val updated = testDAO.question(addedQ.id)
        assertNotEquals(original, updated)
        assertNull(updated?.review)
        assertEquals(original?.content, updated?.content)
    }
}