# Diamond Charts

[![ver](https://img.shields.io/maven-central/v/com.diamondedge/charts-android)](https://repo1.maven.org/maven2/com/diamondedge/charts-android/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Kotlin](https://img.shields.io/badge/Compose-1.4.4-blue.svg)](https://developer.android.com/jetpack/androidx/releases/compose-ui)
[![License](https://img.shields.io/badge/License-Apache--2.0-green)](http://www.apache.org/licenses/LICENSE-2.0)

Diamond Charts is a charting library for Android Jetpack Compose.

## Setup

The library is available from the Maven Central repository with the current version
of ![ver](https://img.shields.io/maven-central/v/com.diamondedge/charts-android)

build.gradle(.kts):

```kotlin
    dependencies {
        api("com.diamondedge:charts-android:$charts_version")
    }
```

## Sample Usage

### Builtin Composable

You can use a builtin composable like this:

```kotlin
@Preview
@Composable
private fun LineGraphMultiPreview() {
    LineGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 2),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}
```

![line](https://user-images.githubusercontent.com/1443778/230458138-969ff1a7-b5ad-4504-99c4-90a39bb8874a.png)

More builtin samples can be [found here](Samples.md)

### Custom Composable

It is expected in most cases that you will create your own Composable that will create the chart. The following is an example of creating
a line graph using a function to compute the data points.

```kotlin
@Composable
fun FunctionGraph(
    minX: Double,
    maxX: Double,
    modifier: Modifier = Modifier,
    margins: Margins = Margins.default,
    fn: (Double) -> Double,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        // create an instance of the Charts container
        val charts = Charts(size.width, size.height, margins, Charts.LEGEND_NONE)
        
        // add the desired charts and graphs to the container
        // if multiple are added they will be drawn on top of each other
        charts.add(XYGraph(createData(fn, minX, maxX)))

        // make any desired changes to the vertical and horizontal axis
        charts.vertAxis?.apply {
            majorTickLabelPosition = TickLabelPosition.BelowTick
        }
        charts.horizontalAxis?.apply {
            majorTickLabelPosition = TickLabelPosition.RightOfTick
        }

        // do the drawing of chart
        // you can add custom drawing on top of the charts to provide even more customizations
        drawIntoCanvas { canvas ->
            charts.draw(ComposeGC(canvas, density))
        }
    }
}

private fun createData(fn: (Double) -> Double, minX: Double, maxX: Double): ChartData {
    val data = DefaultXYData("fn")
    data.dataCount = 100
    val xInc = (maxX - minX) / data.dataCount
    for (i in 0 until data.dataCount) {
        val x = minX + (i + 1) * xInc
        data.setValue(i, x, fn(x))
    }
    return data
}

@Preview
@Composable
private fun FunctionGraphPreview() {
    FunctionGraph(
        -1.5,
        3.5,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        margins = Margins.wideRight
    ) { x ->
        (x + 1) * (x - 2) * (x - 2)
    }
}
```

![function](https://user-images.githubusercontent.com/1443778/230459075-0113b77a-1a0d-421e-bca4-8e7f1b9ca8e1.png)

## Compose and Kotlin version support

| Charts version | Compose BOM version | Kotlin version |
|----------------|---------------------|----------------|
| 1.6.2          | 2024.09.00          | 1.9.20         |
| 1.5.3          | 2024.06.00          | 1.9.20         |
| 1.5.2          | 2023.08.00          | 1.8.22         |
