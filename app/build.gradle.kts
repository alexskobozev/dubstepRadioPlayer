plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.wishnewjam.dubstepfm"
        minSdk = 21
        targetSdk = 30
        versionCode = 10306
        versionName = "1.3.6"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro")
        }
    }
    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = KOTLIN_VERSION
        kotlinCompilerExtensionVersion = AndroidX.COMPOSE
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation("androidx.multidex:multidex:${AndroidX.MULTIDEX}")
    implementation("androidx.appcompat:appcompat:${AndroidX.APPCOMPAT}")
    implementation("androidx.media:media:${AndroidX.MEDIA}")
    implementation("androidx.constraintlayout:constraintlayout:${AndroidX.CONSTRAINT}")
    implementation("androidx.preference:preference-ktx:${AndroidX.PREFERENCES}")
    implementation("com.google.android.exoplayer:exoplayer-core:${Libs.EXOPLAYER}")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:${Libs.LEAK}")

    implementation("androidx.core:core-ktx:${Libs.KTX}")

    implementation("com.google.firebase:firebase-core:${Libs.FIREBASE}")
//    implementation("com.google.firebase:firebase-crashlytics:${Libs.FIREBASE_CRASH}") todo

    implementation("androidx.lifecycle:lifecycle-extensions:${AndroidX.LIFECYCLE}")

    implementation("com.afollestad.material-dialogs:core:${Libs.MATERIAL_DIALOGS}")

    implementation("androidx.compose.ui:ui:${AndroidX.COMPOSE}")
    implementation("androidx.compose.ui:ui-tooling:${AndroidX.COMPOSE}")
    implementation("androidx.compose.foundation:foundation:${AndroidX.COMPOSE}")
    implementation("androidx.compose.material:material:${AndroidX.COMPOSE}")

    implementation("androidx.compose.material:material-icons-core:${AndroidX.COMPOSE}")
    implementation("androidx.compose.material:material-icons-extended:${AndroidX.COMPOSE}")
    implementation("androidx.compose.runtime:runtime-livedata:${AndroidX.COMPOSE}")
    implementation("androidx.compose.runtime:runtime:${AndroidX.COMPOSE}")
    implementation("androidx.activity:activity-compose:${AndroidX.ACTIVITY_COMPOSE}")

    implementation("com.google.dagger:hilt-android:${Libs.HILT}")
    kapt("com.google.dagger:hilt-android-compiler:${Libs.HILT}")

}
apply(mapOf("plugin" to "com.google.gms.google-services"))
