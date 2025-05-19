import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.diaviseo"
    compileSdk = 36 // 35 -> 36

    defaultConfig {
        applicationId = "com.example.diaviseo"
        minSdk = 28
        targetSdk = 36   // 개발 시 이용하고 있는 라이브러리 버전
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // local.properties 읽기
        val localProperties = Properties()
        localProperties.load(FileInputStream(rootProject.file("local.properties")))
        val serverClientId = localProperties.getProperty("GOOGLE_SERVER_CLIENT_ID") ?: ""
        val baseUrl = localProperties.getProperty("BASE_URL") ?:""

        // BuildConfig에 추가
        buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", serverClientId)
        buildConfigField("String", "BASE_URL", "\"${baseUrl}\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file("../diaviseo_release.jks")
            storePassword = "diaviseo12!"
            keyAlias = "diaviseo_release_key"
            keyPassword = "diaviseo12!"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
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

    // Jetpack Compose 활성화
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

// 외부 라이브러리를 이용 시 gradle 파일의 dependencies 등록
dependencies {
    // Import the Firebase BoM
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation(platform("androidx.compose:compose-bom:2025.03.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.foundation:foundation")


    implementation("androidx.navigation:navigation-compose:2.7.7")   // Jetpack Navigation

    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")   // 코루틴
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.datastore:datastore-preferences:1.1.0")    // datastore
    implementation("androidx.work:work-runtime-ktx:2.8.1")    // WorkManager

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.35.0-alpha") // System UI 제어

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    implementation("com.patrykandpatrick.vico:compose:1.13.1")
    implementation("com.patrykandpatrick.vico:core:1.13.1")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.tooling.preview.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //헬스 커넥트
    implementation("androidx.health.connect:connect-client:1.1.0-rc01")

    implementation("androidx.core:core-splashscreen:1.0.1")
}