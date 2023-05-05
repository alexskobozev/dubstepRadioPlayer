@file:Suppress("UnstableApiUsage")

include(":di")
include(":metadata:data")
include(":metadata:domain")


include(":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven(url = "https://dl.bintray.com/drummer-aidan/maven/")
    }
}
