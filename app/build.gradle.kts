plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.mallow.newsapp"
    compileSdk = 35
    buildFeatures {
        compose = true
    }

    buildFeatures.dataBinding = true
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.mallow.newsapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.retrofit2)
    implementation(libs.androidx.convertermoshi)
    implementation(libs.androidx.moshikotlin)
    implementation(libs.androidx.okhttp)
    implementation(libs.androidx.convertergson)
    implementation(libs.androidx.logginginterceptor)
    implementation(libs.androidx.adapterrxjava2)

    implementation(libs.androidx.lifecyclelivedataktx)
    implementation(libs.androidx.lifecycleruntimektx)
    implementation(libs.androidx.lifecycleviewmodelktx)

    implementation(libs.androidx.rxjava)
    implementation(libs.androidx.rxjava2)
    implementation(libs.androidx.rxandroid)

    implementation(libs.androidx.navigationcompose)
    implementation(libs.androidx.navigationfragmentktx)
    implementation(libs.androidx.navigationuiktx)
    implementation(libs.androidx.legacysupportv4)

    implementation(libs.androidx.timber)

    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.ui.tooling.preview)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.coil.compose)

    implementation(libs.androidx.accompanist.swiperefresh)
    implementation(libs.androidx.accompanist.pager)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}