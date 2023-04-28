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
    versionCatalogs {
        create("libs") {
            // from(files("gradle/libs.versions.toml"))
        }
    }
}
