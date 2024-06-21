plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.system.java.get().toInt())
}

dependencies {
    api(project(":di"))

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel)
}
