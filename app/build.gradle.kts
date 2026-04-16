plugins {
    id("com.android.application")
}

android {
    namespace = "com.pawtrip.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pawtrip.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.fragment:fragment:1.6.2")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // OSMDroid — free OpenStreetMap (no API key needed)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // GPS location only (no Maps SDK needed)
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // JavaMail for email sending
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    // WorkManager for background RSS refresh
    implementation("androidx.work:work-runtime:2.9.0")

    // OkHttp for network requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Glide — efficient image loading for pet photo thumbnail
    implementation("com.github.bumptech.glide:glide:4.16.0")
}