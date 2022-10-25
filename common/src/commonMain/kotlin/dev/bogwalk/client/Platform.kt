package dev.bogwalk.client

import io.ktor.client.*
import io.ktor.client.engine.*

expect fun httpClient(
    engine: HttpClientEngine? = null,
    config: HttpClientConfig<*>.() -> Unit = {}
): HttpClient