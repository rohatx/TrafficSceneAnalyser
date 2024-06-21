plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.trafficsceneanalyser"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trafficsceneanalyser"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Glide-Bibliotheken
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)  // Verwende `annotationProcessor` für Java-Projekte

    // Volley-Bibliothek
    implementation("com.android.volley:volley:1.2.1")
    // Floatingbutton
    implementation ("com.google.android.material:material:1.3.0")
    // ViewPager
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    // Camera Dependencies
    implementation("androidx.camera:camera-core:1.2.2")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")
}
