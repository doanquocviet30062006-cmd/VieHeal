pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VieHealMobile"

include(
    ":app",
    ":core:common",
    ":core:model",
    ":core:designsystem",
    ":core:ui",
    ":core:navigation",
    ":core:network",
    ":core:security",
    ":feature:auth",
    ":feature:home",
)
