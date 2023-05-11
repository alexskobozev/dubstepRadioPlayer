plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    compileSdk = libs.versions.system.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.wishnewjam.dubstepfm"
        minSdk = libs.versions.system.min.sdk.get().toInt()
        targetSdk = libs.versions.system.target.sdk.get().toInt()
        versionCode = 10306
        versionName = "1.3.6"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.system.java.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.system.java.get().toInt())
    }
    kotlinOptions {
        jvmTarget = libs.versions.system.java.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
    namespace = "com.wishnewjam.dubstepfm"
}

kotlin {
    jvmToolchain(libs.versions.system.java.get().toInt())
}

dependencies {
    implementation(project(":playback:data"))
    implementation(project(":playback:presentation"))
    implementation(project(":metadata:data"))
    implementation(project(":stream:data"))
    implementation(project(":di"))

    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.kotlin.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui)

    implementation(libs.firebase.crashlytics.ktx)

    implementation(libs.kotlin.coroutines.core)

    implementation(libs.libraries.dagger)
    implementation(libs.libraries.dagger.android)
    kapt(libs.libraries.dagger.compiler)

    implementation(libs.libraries.material.dialogs)
    implementation(libs.libraries.timber)
    implementation(libs.libraries.wang)

    debugImplementation(libs.libraries.leak)
}
