plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.system.java.get().toInt())
}

dependencies {
    api(project(":di"))
    api(project(":stream:domain"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel)
}
