plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    compileSdkVersion(30)

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.wishnewjam.dubstepfm"
        minSdkVersion(16)
        targetSdkVersion(30)
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
    implementation(libs.libraries.multidex)
    implementation(libs.androidx.appcompat)
    implementation ("androidx.media:media:${AndroidX.MEDIA}")
    implementation ("androidx.constraintlayout:constraintlayout:${AndroidX.CONSTRAINT}")
    implementation ("com.google.android.exoplayer:exoplayer-core:${Libs.EXOPLAYER}")

    debugImplementation ("com.squareup.leakcanary:leakcanary-android:${Libs.LEAK}")

    implementation ("com.wang.avi:library:${Libs.WANG}")
    implementation ("androidx.core:core-ktx:${Libs.KTX}")

    implementation ("com.google.firebase:firebase-core:${Libs.FIREBASE}")
    implementation ("com.google.firebase:firebase-crashlytics:${Libs.FIREBASE_CRASH}")

    implementation ("androidx.lifecycle:lifecycle-extensions:${AndroidX.LIFECYCLE}")

    implementation ("com.afollestad.material-dialogs:core:${Libs.MATERIAL_DIALOGS}")
}
// apply(mapOf("plugin" to "com.google.gms.google-services"))
