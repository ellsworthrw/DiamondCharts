# iosApp — iOS Swift/SwiftUI shell

This directory holds the iOS application shell for the Diamond Charts sample. The Compose UI itself
lives in `:sampleApp:shared`, which produces a static framework named **`ComposeApp`** for the
`iosArm64` and `iosSimulatorArm64` targets.

The Xcode project (`iosApp.xcodeproj`) **is** checked in and ready to build — no manual setup is
required beyond opening it. It is wired to build and embed the Kotlin framework automatically via a
"Compile Kotlin Framework" run-script build phase that invokes
`./gradlew :sampleApp:shared:embedAndSignAppleFrameworkForXcode`.

## Layout

```
iosApp/
├── iosApp.xcodeproj/          # the Xcode project (tracked)
├── Configuration/
│   └── Config.xcconfig        # TEAM_ID / BUNDLE_ID / APP_NAME
└── iosApp/
    ├── iOSApp.swift           # @main SwiftUI App
    ├── ContentView.swift      # hosts MainViewControllerKt.MainViewController()
    ├── Info.plist
    ├── Assets.xcassets/
    └── Preview Content/
```

## Run

### From Xcode

1. Open `iosApp.xcodeproj`.
2. Select the **iosApp** scheme and an **Apple-Silicon iOS Simulator** (the project excludes the
   `x86_64` simulator slice — see note below).
3. Run. The build phase compiles the `ComposeApp` framework first, then the app.

### From the command line (simulator)

```bash
xcodebuild -project sampleApp/iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -configuration Debug \
  -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

## Notes

- **Running on a physical device** requires signing: set `TEAM_ID` in `Configuration/Config.xcconfig`
  to your Apple Developer Team ID. Simulator builds need no team.
- **Apple-Silicon only on the simulator.** `:sampleApp:shared` configures `iosArm64` +
  `iosSimulatorArm64` but not the legacy Intel `iosX64` (navigation3 publishes no `iosX64` variant),
  so the project sets `EXCLUDED_ARCHS[sdk=iphonesimulator*] = x86_64`. Build simulator targets on an
  Apple-Silicon Mac.
- The framework exports `MainViewController()` from
  `sampleApp/shared/src/iosMain/kotlin/com/diamondedge/chartapp/MainViewController.kt`; `ContentView`
  wraps it in a `UIViewControllerRepresentable`.
- To sanity-check just the Kotlin side links:
  ```bash
  ./gradlew :sampleApp:shared:linkDebugFrameworkIosSimulatorArm64
  ```