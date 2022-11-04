package dev.bogwalk.databases

import kotlinx.coroutines.runBlocking
import kotlin.test.*

// Unless run separately, every test but the first throws a SQLException (but tests still pass)
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
        val deckId = testDAO.addNewDeck("Test Deck")

        assertTrue { testDAO.allQuestions(deckId).isEmpty() }

        val name = "Fake question"
        testDAO.addNewQuestion(
            deckId, "$name 1", "A", "B", "C", "D", "A"
        )

        for (i in 2..4) {
            assertNotNull(testDAO.addNewQuestion(
                deckId, "$name $i", "A", "B", "C", "D", "A"
            ))
        }
        val questions = testDAO.allQuestions(deckId)

        assertEquals(4, questions.size)
        assertEquals("$name 4", questions.first().content)
        assertEquals("$name 1", questions.last().content)
    }

    @Test
    fun `selection by id returns existing new Question`() = runBlocking {
        val deckId = testDAO.addNewDeck("Test Deck")

        val content = "Fake question"
        testDAO.addNewQuestion(
            deckId, content, "A", "B", "C", "D", "A"
        )

        val result = testDAO.question(deckId)

        assertEquals(content, result?.content)
        assertNull(result?.review)
    }

    @Test
    fun `selection by id returns null for non-existent id`() = runBlocking {
        assertNull(testDAO.question(1))

        val deckId = testDAO.addNewDeck("Test Deck")
        testDAO.addNewQuestion(
            deckId, "Fake question", "A", "B", "C", "D", "A"
        )

        assertNull(testDAO.question(9999))
    }

    @Test
    fun `edit by id correctly edits Question if it exists`() = runBlocking {
        val deckId = testDAO.addNewDeck("Test Deck")

        assertFalse { testDAO.editQuestion(
            1, "Fake question", "A", "B", "C", "D", "A"
        ) }

        val originalId = testDAO.addNewQuestion(
            deckId, "Fake question", "A", "B", "C", "D", "A"
        )

        val newAnswer = "C"
        assertTrue { testDAO.editQuestion(
            originalId,  "Fake question", "A", "B", "C", "D", newAnswer
        ) }

        val updated = testDAO.question(originalId)

        assertEquals(originalId, updated?.id)
        assertEquals(newAnswer, updated?.expectedAnswer)
        assertNotEquals("A", updated?.expectedAnswer)
    }

    @Test
    fun `editReview correctly edits Question Review if it exists`() = runBlocking {
        val deckId = testDAO.addNewDeck("Test Deck")

        assertFalse { testDAO.editReview(1, "Explanation", emptyList()) }

        val originalId = testDAO.addNewQuestion(
            deckId, "Fake question", "A", "B", "C", "D", "A"
        )
        val original = testDAO.question(originalId)
        assertNull(original?.review)

        assertTrue { testDAO.editReview(originalId,  "Explanation", emptyList()) }

        val updated = testDAO.question(originalId)

        assertEquals(originalId, updated?.id)
        assertEquals(original?.content, updated?.content)

        val firstReview = updated?.review
        assertNotNull(firstReview)

        assertTrue { testDAO.editReview(originalId,  "", listOf("A" to "ALink", "B" to "BLink")) }

        val secondReview = testDAO.question(originalId)?.review
        assertNotNull(secondReview)

        assertNotEquals(firstReview, secondReview)
        assertTrue { secondReview.content.isEmpty() }
        assertEquals(2, secondReview.references.size)
    }

    @Test
    fun `delete by id correctly deletes Question if it exists`() = runBlocking {
        val deckId = testDAO.addNewDeck("Test Deck")
        assertFalse { testDAO.deleteQuestion(1) }

        repeat (3) {
            testDAO.addNewQuestion(
                deckId, "Fake question", "A", "B", "C", "D", "A"
            )
        }
        val questions = testDAO.allQuestions(deckId)
        assertEquals(3, questions.size)
        val questionToDelete = questions.last()

        assertTrue { testDAO.deleteQuestion(questionToDelete.id) }

        val questionsUpdated = testDAO.allQuestions(deckId)
        assertEquals(2, questionsUpdated.size)
        assertFalse { questionToDelete in questionsUpdated }
    }

    @Test
    fun `deleteReview correctly deletes Review only if it exists`() = runBlocking {
        val deckId = testDAO.addNewDeck("Test Deck")
        assertFalse { testDAO.deleteReview(1) }

        val addedQId = testDAO.addNewQuestion(
            deckId, "Fake question", "A", "B", "C", "D", "A"
        )
        assertTrue {
            testDAO.editReview(addedQId, "Explanation", listOf("A" to "ALink", "B" to "BLink"))
        }

        val original = testDAO.question(addedQId)
        assertEquals(2, original?.review?.references?.size)
        assertTrue { testDAO.deleteReview(addedQId) }

        val updated = testDAO.question(addedQId)
        assertNotEquals(original, updated)
        assertNull(updated?.review)
        assertEquals(original?.content, updated?.content)
    }
}