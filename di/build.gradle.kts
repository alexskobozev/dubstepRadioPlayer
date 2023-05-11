plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

kotlin {
    jvmToolchain(libs.versions.system.java.get().toInt())
}

dependencies {
}
