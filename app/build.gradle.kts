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

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

tasks.withType<Test> {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

dependencies {
    // Core Libraries
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
    implementation(libs.androidx.espresso.idling.resource)

    // Testing Libraries
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.espresso.core.v361)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.androidx.espresso.idling.resource)
    androidTestImplementation(libs.androidx.idling.concurrent)
    androidTestImplementation(libs.androidx.runner)


    // Room and Database
    ksp(libs.androidx.room.compiler.v250)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Networking
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation(libs.retrofit)
    implementation(libs.converter.gson.v290)
    implementation(libs.converter.gson)

    // Other Libraries
    implementation(libs.glide)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.maps.v1820)
    implementation(libs.play.services.location)
    implementation(libs.room.paging)

    // Additional Libraries
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing.v210)
    testImplementation(libs.kotlinx.coroutines.test.v161)

    testImplementation(libs.mockk)
    implementation(libs.byte.buddy.v11511)
    testImplementation(libs.byte.buddy.agent.v11511)

}
