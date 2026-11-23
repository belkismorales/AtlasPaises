import java.util.Properties

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")

    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

val apiKey = localProperties.getProperty(
    "REST_COUNTRIES_API_KEY", ""
)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.example.atlaspaises"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.atlaspaises"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "REST_COUNTRIES_API_KEY",
            "\"$apiKey\""
        )

    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    ksp(libs.androidx.room.compiler)

// Retrofit: conexión con la API
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

// Gson: conversión de JSON a Kotlin
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

// OkHttp: envío de la API Key
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Room: base de datos SQLite
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.1")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.compose.material:material-icons-extended")



}