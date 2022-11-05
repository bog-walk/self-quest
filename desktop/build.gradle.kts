import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "dev.bogwalk"
version = "1.0"

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)

    testImplementation(kotlin("test"))
    testImplementation(compose("org.jetbrains.compose.ui:ui-test-junit4"))
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
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