plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    compileSdk = 35

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.wishnewjam.dubstepfm"
        minSdk = 21
        targetSdk = 35
        versionCode = 10308
        versionName = "1.3.8"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    namespace = "com.wishnewjam.dubstepfm"
}

dependencies {
    implementation(libs.libraries.multidex)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.constraint)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.exoplayer.core)
    implementation(libs.libraries.material.dialogs)
    implementation(libs.libraries.timber)

    implementation(platform(libs.firebase.billOfMaterials))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.material)

    debugImplementation (libs.libraries.leak)
}
