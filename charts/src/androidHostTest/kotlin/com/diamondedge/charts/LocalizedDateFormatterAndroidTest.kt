/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Runs the shared [LocalizedDateFormatterTest] suite on the Android target. Android-host (JVM) unit
 * tests run against a stub `android.jar` whose methods throw, so `LocalizedDateFormatter`'s use of
 * `android.text.format.DateFormat.getBestDateTimePattern` would fail. Robolectric supplies the real
 * framework classes, letting the exact same locale assertions execute against the Android `actual`s.
 *
 * The common [LocalizedDateFormatterTest] is excluded from this source set's run (see the
 * `testAndroidHostTest` filter in `build.gradle.kts`); only this Robolectric-driven subclass runs.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class LocalizedDateFormatterAndroidTest : LocalizedDateFormatterTest()