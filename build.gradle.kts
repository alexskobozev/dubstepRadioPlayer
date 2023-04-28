buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:${Plugins.AGP}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION")
        classpath("com.google.gms:google-services:${Plugins.GOOGLE_SERVICES}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Plugins.FIREBASE_CRASHLITYCS}")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version KOTLIN_VERSION
}
