plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
}
buildscript {
    val compose_compiler by extra("1.5.15")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}