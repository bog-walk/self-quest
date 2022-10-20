package dev.bogwalk.routes

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource(Routes.ALL_DECKS)
class Decks {
    @Serializable
    @Resource(Routes.BY_ID)
    class Id(val parent: Decks, val id: String) {
        @Serializable
        @Resource(Routes.ALL_QUESTIONS)
        class Questions(val parent: Decks) {
            @Serializable
            @Resource(Routes.BY_ID)
            class Id(val parent: Decks, val id: String)
        }
    }
}