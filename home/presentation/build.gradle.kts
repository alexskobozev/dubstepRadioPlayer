plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.wishnewjam.home.presentation"
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
    implementation(project(":commons:design"))
    implementation(project(":metadata:domain"))
    implementation(project(":playback:domain"))
    implementation(project(":playback:presentation"))
    implementation(libs.androidx.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui)

    implementation(libs.libraries.dagger)
    implementation(libs.libraries.dagger.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)

    ksp(libs.libraries.dagger.compiler)
    implementation(libs.libraries.timber)
}
