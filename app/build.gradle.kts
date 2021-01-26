plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation ("androidx.multidex:multidex:${AndroidX.MULTIDEX}")
    implementation ("androidx.appcompat:appcompat:${AndroidX.APPCOMPAT}")
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
apply(mapOf("plugin" to "com.google.gms.google-services"))
