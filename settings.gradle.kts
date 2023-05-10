@file:Suppress("UnstableApiUsage")



include(":di")
include(":commons:android")
include(":metadata:data")
include(":metadata:domain")
include(":playback:data")
include(":playback:domain")
include(":playback:presentation")
include(":stream:data")
include(":stream:domain")



include(":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven(url = "https://dl.bintray.com/drummer-aidan/maven/")
    }
}
