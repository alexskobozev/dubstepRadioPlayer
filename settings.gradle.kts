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
        google()
        maven(url = "https://dl.bintray.com/drummer-aidan/maven/")
    }
}
