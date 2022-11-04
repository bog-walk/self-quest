package dev.bogwalk.routes

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource(Routes.ALL_DECKS)
class Decks {
    @Serializable
    @Resource(Routes.BY_D_ID)
    class DeckId(val parent: Decks = Decks(), val id_d: Int) {
        @Serializable
        @Resource(Routes.ALL_QUESTIONS)
        class Questions(val parent: DeckId) {
            @Serializable
            @Resource(Routes.BY_Q_ID)
            class QuestionId(val parent: Questions, val id_q: Int) {
                @Serializable
                @Resource(Routes.Q_REVIEW)
                class QReview(val parent: QuestionId)
            }
        }
    }
}

@Serializable
@Resource(Routes.SHUTDOWN)
class ServerShutdown