plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.vieheal.mobile.core.security"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(libs.appauth)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit4)
    testImplementation(libs.coroutines.test)
}
