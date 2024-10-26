plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.company.plantshop_nguyentiendung_se171710"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.company.plantshop_nguyentiendung_se171710"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\ntig\\Downloads\\ZaloPayLib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation("jp.wasabeef:glide-transformations:4.3.0")

    implementation(libs.firebase.firestore)
}