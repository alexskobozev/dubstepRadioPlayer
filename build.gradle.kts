buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION")
        classpath("com.google.gms:google-services:${Plugins.GOOGLE_SERVICES}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Plugins.FIREBASE_CRASHLITYCS}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Libs.HILT}")

    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version KOTLIN_VERSION
    id("com.github.ben-manes.versions") version Plugins.VERSIONS
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE",
            "FINAL",
            "GA").any {
        version.toUpperCase()
                .contains(it)
    }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}