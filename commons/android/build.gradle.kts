plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.wishnewjam.commons.android"
    compileSdk = libs.versions.system.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.system.min.sdk.get().toInt()
        targetSdk = libs.versions.system.target.sdk.get().toInt()
    }
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":di"))
}
