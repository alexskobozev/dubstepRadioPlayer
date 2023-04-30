plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    compileSdkVersion(33)

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.wishnewjam.dubstepfm"
        minSdkVersion(16)
        targetSdkVersion(33)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    namespace = "com.wishnewjam.dubstepfm"
}

dependencies {
    implementation(platform(libs.firebase.billOfMaterials))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraint)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation(libs.firebase.crashlytics.ktx)

    implementation(libs.libraries.material.dialogs)
    implementation(libs.libraries.multidex)
    implementation(libs.libraries.wang)

    debugImplementation (libs.libraries.leak)
}
