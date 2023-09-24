plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.versions)
}

subprojects {
    val buildFile = project.file("build.gradle.kts")
    if (buildFile.exists()) {
        apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                ktfmt()
                ktlint()
//                diktat()
//                prettier()
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint()
            }
        }
    }
}