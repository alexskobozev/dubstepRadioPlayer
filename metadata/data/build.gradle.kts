plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    api(project(":metadata:domain"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.libraries.dagger)
    kapt(libs.libraries.dagger.compiler)
}
