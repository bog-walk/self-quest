pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        val kotlinVersion = extra["kotlinVersion"] as String
        val ktorVersion = extra["ktorVersion"] as String
        val composeVersion = extra["composeVersion"] as String

        kotlin("multiplatform").version(kotlinVersion)
        kotlin("plugin.serialization").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)
        id("io.ktor.plugin").version(ktorVersion)
    }
}

rootProject.name = "self-quest"

include(":common")
include(":desktop")
include(":server")