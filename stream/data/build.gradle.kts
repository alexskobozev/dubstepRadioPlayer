plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

kotlin {
    jvmToolchain(libs.versions.system.java.get().toInt())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.system.java.get().toInt()))
    }
}

dependencies {
    api(project(":stream:domain"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.libraries.dagger)
    kapt(libs.libraries.dagger.compiler)
}
