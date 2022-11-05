import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val hikariVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

group = "dev.bogwalk"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation(project(":common"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    testLogging {
        showStackTraces = true
        showStandardStreams = true
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
    }
}