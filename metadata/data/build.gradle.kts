plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
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
    api(project(":metadata:domain"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.libraries.dagger)
    ksp(libs.libraries.dagger.compiler)
}
