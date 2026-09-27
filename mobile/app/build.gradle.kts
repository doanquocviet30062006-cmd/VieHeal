plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

fun String.asBuildConfigString(): String = "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

val oidcIssuer = providers.gradleProperty("VIEHEAL_OIDC_ISSUER").orElse("")
val oidcClientId = providers.gradleProperty("VIEHEAL_OIDC_CLIENT_ID").orElse("")
val oidcRedirectUri = providers.gradleProperty("VIEHEAL_OIDC_REDIRECT_URI").orElse("com.vieheal.mobile://oauth2redirect/callback")
val backendBaseUrl = providers.gradleProperty("VIEHEAL_BACKEND_BASE_URL").orElse("")
val redirectScheme = providers.gradleProperty("VIEHEAL_OIDC_REDIRECT_SCHEME").orElse("com.vieheal.mobile")

android {
    namespace = "com.vieheal.mobile"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.vieheal.mobile"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["appAuthRedirectScheme"] = redirectScheme.get()
        buildConfigField("String", "OIDC_ISSUER", oidcIssuer.get().asBuildConfigString())
        buildConfigField("String", "OIDC_CLIENT_ID", oidcClientId.get().asBuildConfigString())
        buildConfigField("String", "OIDC_REDIRECT_URI", oidcRedirectUri.get().asBuildConfigString())
        buildConfigField("String", "BACKEND_BASE_URL", backendBaseUrl.get().asBuildConfigString())
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:security"))
    implementation(project(":core:ui"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))

    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.appauth)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit4)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
