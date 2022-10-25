package dev.bogwalk.client

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.java.*

actual fun httpClient(
    engine: HttpClientEngine?,
    config: HttpClientConfig<*>.() -> Unit
): HttpClient {
    return if (engine == null) {
        HttpClient(Java) { config(this) }
    } else {
        HttpClient(engine) { config(this) }
    }
}