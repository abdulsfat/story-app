plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.submission.submissionstoryapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.submission.submissionstoryapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.material)
    implementation(libs.material.v180)
    implementation(libs.androidx.paging.runtime.ktx)

    ksp(libs.androidx.room.compiler.v250)

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation (libs.retrofit)
    implementation (libs.converter.gson.v290)
    implementation (libs.converter.gson)
    implementation (libs.glide)

    implementation(libs.play.services.maps)
    implementation(libs.play.services.maps.v1820)
    implementation(libs.play.services.location)

    implementation(libs.room.paging)


}