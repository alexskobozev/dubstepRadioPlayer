plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.wishnewjam.playback.data"
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

    api(project(":playback:domain"))
    implementation(project(":commons:android"))
    implementation(project(":metadata:domain"))
    implementation(project(":stream:domain"))

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.libraries.dagger)
    implementation(libs.libraries.dagger.android)
    ksp(libs.libraries.dagger.compiler)
    implementation(libs.libraries.timber)
}
