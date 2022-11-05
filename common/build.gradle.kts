val dateTimeVersion: String by project
val coroutineVersion: String by project
val ktorVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "dev.bogwalk"
version = "1.0"

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                implementation("io.ktor:ktor-client-java:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-resources:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion")
            }
        }
    }
}