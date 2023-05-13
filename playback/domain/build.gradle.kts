plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.wishnewjam.playback.domain"
    compileSdk = libs.versions.system.compile.sdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.system.java.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.system.java.get().toInt())
    }
    kotlinOptions {
        jvmTarget = libs.versions.system.java.get()
    }
}

kotlin {
    jvmToolchain(libs.versions.system.java.get().toInt())
}

dependencies {
    api(project(":di"))

    implementation(libs.androidx.media3.session)
}
