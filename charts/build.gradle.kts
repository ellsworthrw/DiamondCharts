plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    compileSdk = 33
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
        kotlinCompilerExtensionVersion = "${rootProject.extra["compose_compiler"]}"
    }

    buildFeatures {
        compose = true
    }
    namespace = "com.diamondedge.charts"

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
    configurations.configureEach {
        resolutionStrategy {
            // https://issuetracker.google.com/issues/295457468
            force("androidx.emoji2:emoji2:1.3.0")
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("org.lighthousegames:logging-android:1.3.0")
}

tasks {
    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn(dokkaHtml)
        from(dokkaHtml.get().outputDirectory)
    }
}

extra["artifactID"] = "charts-android"
extra["artifactVersion"] = "1.5.0"
extra["libraryName"] = "Diamond Charts"
extra["libraryDescription"] = "Diamond Charts: charting library for Android Jetpack Compose"
extra["gitUrl"] = "https://github.com/ellsworthrw/DiamondCharts"

apply(from = "publish.gradle.kts")
