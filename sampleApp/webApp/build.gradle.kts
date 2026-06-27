@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

// wasmJs (browser) entry point for the sample app. Hosts main.kt (src/wasmJsMain) plus the
// index.html that loads it. Depends on :sampleApp:shared. Run with:
//   ./gradlew :sampleApp:webApp:wasmJsBrowserDevelopmentRun
kotlin {
    wasmJs {
        browser {
            // Pin the bundle name so index.html can reference it deterministically.
            commonWebpackConfig {
                outputFileName = "webApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":sampleApp:shared"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
    }
}