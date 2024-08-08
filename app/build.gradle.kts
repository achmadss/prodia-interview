plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.achmadss.prodiainterview"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.achmadss.prodiainterview"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
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

    // koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // material motion compose
    implementation(libs.material.motion.compose.core)

    // voyager
    implementation(libs.voyager.navigator) // Navigator
    implementation(libs.voyager.screenmodel) // Screen Model
    implementation(libs.voyager.bottom.sheet.navigator) // BottomSheetNavigator
    implementation(libs.voyager.tab.navigator) // TabNavigator
    implementation(libs.voyager.transitions) // TabNavigator
    implementation(libs.voyager.koin) // Koin integration

    // material icons
    implementation(libs.androidx.material.icons.extended)

    // coil
    implementation("io.coil-kt:coil-compose:2.4.0") // don't up to 2.5.0 because it breaks

    // paging3
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")

    // okhttp3
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // retrofit2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}