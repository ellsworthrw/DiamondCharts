@file:OptIn(ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    id("com.vanniktech.maven.publish") version "0.37.0"
}

kotlin {
    compilerOptions {
        // expect/actual classes are still flagged Beta by the compiler; this is the documented opt-in.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // AGP 9 KMP library structure: the Android target is configured here via the
    // `com.android.kotlin.multiplatform.library` plugin instead of a separate `android {}` block.
    // No AndroidManifest.xml is needed — the namespace is declared in the DSL.
    androidLibrary {
        namespace = "com.diamondedge.charts"
        compileSdk = 36
        minSdk = 24

        // Enable JVM-host unit tests for the Android target so the commonTest suite also runs
        // against the Android `actual`s (the androidHostTest compilation inherits commonTest).
        withHostTest { }

        optimization {
            consumerKeepRules.file("proguard.txt")
            consumerKeepRules.publish = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.logging)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // The Android host (JVM) unit tests run the common suite under Robolectric so the Android
        // `actual`s execute against real framework classes instead of the throwing android.jar stubs.
        getByName("androidHostTest") {
            dependencies {
                implementation(libs.kotlin.test.junit)
                implementation(libs.robolectric)
            }
        }

        // Intermediate source set for every Skia-backed target (desktop + ios + wasmJs):
        // provides org.jetbrains.skia.* based rendering actuals.
        val skikoMain = create("skikoMain") {
            dependsOn(commonMain.get())
        }
        // Intermediate source set for the JVM-backed targets (android + desktop):
        // provides java.text based localized-formatting actuals.
        val jvmAndroidMain = create("jvmAndroidMain") {
            dependsOn(commonMain.get())
        }

        androidMain {
            dependsOn(jvmAndroidMain)
        }
        // jvm (desktop) is in BOTH intermediates — a legal diamond. Rendering actuals live only
        // in skikoMain/androidMain; formatting actuals live only in jvmAndroidMain/iosMain/wasmJsMain,
        // so no target ever sees two actuals for one expect.
        jvmMain {
            dependsOn(jvmAndroidMain)
            dependsOn(skikoMain)
            dependencies {
                // CLDR skeleton -> pattern generation for locale-correct month/year labels on desktop.
                implementation(libs.icu4j)
            }
        }
        iosMain {
            dependsOn(skikoMain)
        }
        wasmJsMain {
            dependsOn(skikoMain)
        }
    }
}

// The common LocalizedDateFormatterTest is compiled into the Android host-test variant too, but its
// Android `actual` needs the real framework, so it must run under Robolectric. Run only the
// Robolectric subclass (LocalizedDateFormatterAndroidTest) here and skip the bare common class.
tasks.withType<Test>().configureEach {
    if (name == "testAndroidHostTest") {
        filter {
            isFailOnNoMatchingTests = false
            excludeTestsMatching("com.diamondedge.charts.LocalizedDateFormatterTest")
        }
    }
}

extra["artifactId"] = "charts"
extra["artifactVersion"] = "2.0.0"
extra["libraryName"] = "Diamond Charts"
extra["libraryDescription"] = "Diamond Charts: a Kotlin Multiplatform charting library for Compose (Android, iOS, Desktop, Web)"
extra["gitUrl"] = "https://github.com/ellsworthrw/DiamondCharts"

// Publishing
// defined in project's gradle.properties
val groupId = project.property("groupId") as String
val licenseName = project.property("licenseName") as String
val licenseUrl = project.property("licenseUrl") as String
// optional properties
val orgId = project.findProperty("orgId") as String?
val orgName = project.findProperty("orgName") as String?
val orgUrl = project.findProperty("orgUrl") as String?
val developerName = project.findProperty("developerName") as String?
val developerId = project.findProperty("developerId") as String?

val artifactId = extra["artifactId"] as String
val artifactVersion = extra["artifactVersion"] as String
val libraryName = extra["libraryName"] as String
val libraryDescription = extra["libraryDescription"] as String
val gitUrl = extra["gitUrl"] as String

project.group = groupId
project.version = artifactVersion

mavenPublishing {
    configure(KotlinMultiplatform(javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = SourcesJar.Sources()))
    coordinates(groupId = groupId, artifactId = artifactId, version = artifactVersion)
    pom {
        name.set(libraryName)
        description.set(libraryDescription)
        url.set(gitUrl)

        licenses {
            license {
                name.set(licenseName)
                url.set(licenseUrl)
            }
        }
        scm {
            url.set(gitUrl)
        }
        developers {
            if (!developerId.isNullOrEmpty()) {
                developer {
                    id.set(developerId)
                    name.set(developerName)
                }
            }
            if (!orgId.isNullOrEmpty()) {
                developer {
                    id.set(orgId)
                    name.set(orgName)
                    organization.set(orgName)
                    organizationUrl.set(orgUrl)
                }
            }
        }
        if (!orgName.isNullOrEmpty()) {
            organization {
                name.set(orgName)
                if (!orgUrl.isNullOrEmpty())
                    url.set(orgUrl)
            }
        }
    }
}
