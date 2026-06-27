pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    // PREFER_SETTINGS (not FAIL_ON_PROJECT_REPOS): the Kotlin wasm/js toolchain downloads Node.js
    // and Yarn from their own distribution sites; those repos are declared below so they resolve
    // through settings instead of being rejected as project-added repositories.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        ivy("https://nodejs.org/dist/") {
            name = "Node.js Distributions"
            patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
            metadataSources { artifact() }
            content { includeModule("org.nodejs", "node") }
        }
        ivy("https://github.com/yarnpkg/yarn/releases/download") {
            name = "Yarn Distributions"
            patternLayout { artifact("v[revision]/[artifact]-v[revision].[ext]") }
            metadataSources { artifact() }
            content { includeModule("com.yarnpkg", "yarn") }
        }
        ivy("https://github.com/WebAssembly/binaryen/releases/download") {
            name = "Binaryen Distributions"
            patternLayout { artifact("version_[revision]/[artifact]-version_[revision]-[classifier].[ext]") }
            metadataSources { artifact() }
            content { includeModule("com.github.webassembly", "binaryen") }
        }
    }
}
rootProject.name = "Charts"
include(":charts")
// Sample app, split into a shared KMP UI library + thin per-platform entry points, nested under sampleApp/.
// iosApp/ is a Swift/SwiftUI Xcode shell (not a Gradle module) — see sampleApp/iosApp/README.md.
include(":sampleApp:shared")
include(":sampleApp:androidApp")
include(":sampleApp:desktopApp")
include(":sampleApp:webApp")