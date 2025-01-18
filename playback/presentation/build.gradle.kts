plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.wishnewjam.playback.presentation"
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
                "proguard-rules.pro",
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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.system.java.get().toInt()))
    }
}

dependencies {

    implementation(project(":commons:android"))
    implementation(project(":metadata:domain"))
    implementation(project(":playback:domain"))

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.libraries.dagger)
    implementation(libs.libraries.dagger.android)
    implementation(project(mapOf("path" to ":playback:data")))
    implementation(libs.androidx.core)
    ksp(libs.libraries.dagger.compiler)
    implementation(libs.libraries.timber)
}
