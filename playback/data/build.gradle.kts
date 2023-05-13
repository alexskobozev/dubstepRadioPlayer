plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)

}

android {
    namespace = "com.wishnewjam.playback.data"
    compileSdk = libs.versions.system.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.system.min.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
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
    kapt(libs.libraries.dagger.compiler)
    implementation(libs.libraries.timber)

}
