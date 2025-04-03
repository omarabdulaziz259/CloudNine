import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val apiKey = localProperties.getProperty("API_KEY") ?: ""
val googleApiKey = localProperties.getProperty("GOOGLE_API_KEY") ?: ""

android {
    namespace = "com.example.cloudnine"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.cloudnine"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "$apiKey")
        buildConfigField("String", "GOOGLE_API_KEY", "$googleApiKey")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //nav graph
    val nav_version = "2.8.8"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    // Serialization for NavArgs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")


    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // GSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    //glide
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

    // Room components
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    //view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Need this or MapEffect throws exception.
//    implementation ("androidx.appcompat:appcompat:1.5.1")

    // Compose
//    implementation ("androidx.compose.ui:ui:1.3.2")
//    implementation ("androidx.compose.material:material:1.3.1")

    // Google maps
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    // Google maps for compose
    implementation ("com.google.maps.android:maps-compose:2.8.0")

    // KTX for the Maps SDK for Android
    implementation ("com.google.maps.android:maps-ktx:3.2.1")
    // KTX for the Maps SDK for Android Utility Library
    implementation ("com.google.maps.android:maps-utils-ktx:3.2.1")

    //Google Maps
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.android.libraries.places:places:3.3.0")

}
secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.properties"
}