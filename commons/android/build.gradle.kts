plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.wishnewjam.commons.android"
    compileSdk = libs.versions.system.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.system.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.system.java.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.system.java.get().toInt())
    }
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
    implementation(project(":di"))
}
