plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.dokka") version "1.8.10" apply false
}
buildscript {
    val compose_compiler by extra("1.4.4")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}