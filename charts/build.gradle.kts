import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.compose.compiler)
    id("com.vanniktech.maven.publish") version "0.29.0"
}

android {
    compileSdk = 34
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
    }

    buildFeatures {
        compose = true
    }
    namespace = "com.diamondedge.charts"

    configurations.configureEach {
        resolutionStrategy {
            // https://issuetracker.google.com/issues/295457468
            force("androidx.emoji2:emoji2:1.3.0")
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.logging)
}

tasks {
    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn(dokkaHtml)
        from(dokkaHtml.get().outputDirectory)
    }
}

extra["artifactId"] = "charts-android"
extra["artifactVersion"] = "1.7.0"
extra["libraryName"] = "Diamond Charts"
extra["libraryDescription"] = "Diamond Charts: charting library for Android Jetpack Compose"
extra["gitUrl"] = "https://github.com/ellsworthrw/DiamondCharts"

// Publishing
// defined in project's gradle.properties
val groupId: String by project
val licenseName: String by project
val licenseUrl: String by project
// optional properties
val orgId: String? by project
val orgName: String? by project
val orgUrl: String? by project
val developerName: String? by project
val developerId: String? by project

val artifactId: String by extra
val artifactVersion: String by extra
val libraryName: String by extra
val libraryDescription: String by extra
val gitUrl: String by extra

project.group = groupId
project.version = artifactVersion

mavenPublishing {
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

    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
}
