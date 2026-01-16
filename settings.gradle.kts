include(":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven(url = "https://dl.bintray.com/drummer-aidan/maven/")
    }
}
