plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

val dep = rootProject.ext

android {
    compileSdkVersion("${dep["compileSdkVersion"]}".toInt())
    defaultConfig {
        applicationId = "com.vodolazskiy.forecastapplication"
        minSdkVersion("${dep["minSdkVersion"]}")
        targetSdkVersion("${dep["targetSdkVersion"]}")
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "forecast_key", dep["forecastKey"].toString())
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    androidExtensions {
        isExperimental = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${dep["kotlinVersion"]}")
    implementation("androidx.appcompat:appcompat:${dep["supportLibraryVersion"]}")
    implementation("androidx.core:core-ktx:${dep["ktxVersion"]}")
    implementation("androidx.constraintlayout:constraintlayout:${dep["constraintLayoutVersion"]}")

    //dagger
    implementation("com.google.dagger:dagger:${dep["daggerVersion"]}")
    kapt("com.google.dagger:dagger-compiler:${dep["daggerVersion"]}")
    implementation("com.google.dagger:dagger-android:${dep["daggerVersion"]}")
    implementation("com.google.dagger:dagger-android-support:${dep["daggerVersion"]}")
    kapt("com.google.dagger:dagger-android-processor:${dep["daggerVersion"]}")

    //network
    implementation("com.squareup.okhttp3:okhttp:${dep["okhttpVersion"]}")

    //presentation
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${dep["lifecycleVersion"]}")
    implementation("androidx.lifecycle:lifecycle-extensions:${dep["lifecycleVersion"]}")
    implementation("androidx.recyclerview:recyclerview:${dep["recyclerViewVersion"]}")
    implementation("androidx.cardview:cardview:${dep["cardViewVersion"]}")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${dep["coroutinesVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${dep["coroutinesVersion"]}")

    //play services
    implementation("com.google.android.gms:play-services-location:${dep["playServicesVersion"]}")

    //tests
    testImplementation("junit:junit:${dep["junitVersion"]}")
    testImplementation("io.mockk:mockk:${dep["mockkVersion"]}")
    testImplementation("android.arch.core:core-testing:${dep["archCoreVersion"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${dep["coroutineTestVersion"]}")
}
