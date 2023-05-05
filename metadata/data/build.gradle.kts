plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

dependencies {
    api(project(":metadata:domain"))

    implementation(libs.libraries.dagger)
    kapt(libs.libraries.dagger.compiler)
}
