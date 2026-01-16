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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    packaging {
        resources {
            excludes += "META-INF/atomicfu.kotlin_module"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    namespace = "com.wishnewjam.dubstepfm"
}

dependencies {
    implementation(libs.libraries.multidex)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media)
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

    // Unit test dependencies
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.androidx.core)
    testImplementation(libs.test.androidx.core.ktx)
    testImplementation(libs.test.arch.core)
    testImplementation(libs.test.coroutines)

    // Android instrumented test dependencies
    androidTestImplementation(libs.test.androidx.runner)
    androidTestImplementation(libs.test.androidx.rules)
    androidTestImplementation(libs.test.androidx.ext.junit)
    androidTestImplementation(libs.test.androidx.ext.junit.ktx)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.test.espresso.contrib)
    androidTestImplementation(libs.test.mockk.android)
    androidTestImplementation(libs.test.fragment)
}
