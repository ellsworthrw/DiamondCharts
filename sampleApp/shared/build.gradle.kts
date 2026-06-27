@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    // AGP 9 KMP library structure: the Android target is configured via the
    // `com.android.kotlin.multiplatform.library` plugin (no separate `android {}` block,
    // no AndroidManifest — namespace is declared here).
    androidLibrary {
        namespace = "com.diamondedge.chartapp.shared"
        compileSdk = 36
        minSdk = 24

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()

    // navigation3-runtime 1.1.3 publishes only iosArm64 + iosSimulatorArm64 (no legacy iosX64),
    // so the shared UI omits the Intel-simulator target. Apple-Silicon simulators use iosSimulatorArm64.
    iosArm64()
    iosSimulatorArm64()

    // The iOS app (iosApp/) links this framework; MainViewController.kt in iosMain is its entry point.
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // The web sample (:sampleApp:webApp) consumes this as a wasmJs library; all of the shared UI's
    // dependencies (charts, Compose, Navigation 3, kotlinx-datetime) publish wasmJs variants.
    wasmJs {
        browser()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            // `api` so the entry-point modules (androidApp/desktopApp) and the
            // Android-only cooksession sample can reference com.diamondedge.charts.* directly.
            api(project(":charts"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.ui)
            implementation(libs.compose.tooling.preview)
            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel.navigation3)
            implementation(libs.kotlinx.datetime)
            implementation(libs.logging)
        }

        // ui-tooling (the @Preview renderer) is JVM/Android-only — it has no iOS variant, so it
        // can't live in commonMain. Desktop is where common composables render in the IDE preview.
        jvmMain.dependencies {
            implementation(libs.compose.tooling)
        }
    }
}
