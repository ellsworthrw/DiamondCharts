plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka") version "1.6.20"
    id("maven-publish")
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 21
        consumerProguardFiles("proguard.txt")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
//        useIR = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

    buildFeatures {
        compose = true
    }

//    namespace = "com.diamondedge.charts"

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.1.1")
    implementation("androidx.compose.foundation:foundation:1.1.1")
    implementation("org.lighthousegames:logging-android:1.1.2")
}

tasks {
    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn(dokkaHtml)
        from(dokkaHtml.get().outputDirectory)
    }
}

extra["artifactID"] = "charts-android"
extra["artifactVersion"] = "0.1.0"
extra["libraryName"] = "Diamond Charts"
extra["libraryDescription"] = "Diamond Charts: charting library for Android Jetpack Compose"
extra["gitUrl"] = "https://github.com/ellsworthrw/DiamondCharts"

apply(from = "publish.gradle.kts")
