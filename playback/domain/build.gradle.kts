plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    api(project(":di"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.coroutines.core)
}
