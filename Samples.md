## Samples

### Vertical Bar Chart
```kotlin
@Preview
@Composable
private fun BarChartPreview() {
    BarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.medium
    )
```
![vert-bar](https://user-images.githubusercontent.com/1443778/230460382-73c8d6c5-fbef-4dc2-953e-14e3b6ced61c.png)

### Horizontal Bar Chart
```kotlin
@Preview
@Composable
private fun BarChartHorizontalPreview() {
    BarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        isVertical = false,
        margins = Margins.wide,
        legendPosition = Charts.LEGEND_RIGHT
    )
}
```
![hor-bar](https://user-images.githubusercontent.com/1443778/230460488-0b607557-5f06-454f-82d9-fa6cc0eb0e12.png)


### Pie Chart
```kotlin
@Preview
@Composable
private fun PieChartPreview() {
    PieChart(
        RandomData(DefaultData.SIMPLE_SERIES, 1),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wide,
        legendPosition = Charts.LEGEND_RIGHT
    )
}
```
![pie](https://user-images.githubusercontent.com/1443778/230461254-c1deac91-aac4-41b5-bdeb-54819f7c4e37.png)

### Area Graph
```kotlin
@Preview
@Composable
private fun LineGraphPreview() {
    LineGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 1),
        fillArea = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}
```
![area](https://user-images.githubusercontent.com/1443778/230461745-1ce4401d-170c-428b-a2b3-1abeff4a59a5.png)

### Line Graph
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

### Scatter Graph
```kotlin
@Preview
@Composable
private fun ScatterGraphPreview() {
    LineGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 7),
        drawLine = false,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}
```
![scatter](https://user-images.githubusercontent.com/1443778/230462413-44464753-fb6a-4488-8df1-a76a4ac7e4c4.png)

### Stock Chart
```kotlin
@Preview
@Composable
private fun StockChartPreview() {
    StockChart(
        RandomData(DefaultData.HLOC_SERIES, 2),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        margins = Margins(10f, 10f, 30f, 10f)
    )
}
```
![stock](https://user-images.githubusercontent.com/1443778/230462542-64250697-de43-451d-80c7-fbd26bf8725f.png)

### Vertical Stacked Bar Chart
```kotlin
@Preview
@Composable
private fun BarChartPreview() {
    StackedBarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.medium
    )
}
```
![vert-stacked-bar](https://user-images.githubusercontent.com/1443778/230462894-98a5ef2b-9254-47c9-86da-ad89f61f2349.png)

### Vertical Stacked 100% Bar Chart
```kotlin
@Preview
@Composable
private fun BarChart100PercentPreview() {
    StackedBarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        is100Percent = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.medium
    )
}
```
![bar-100percent](https://user-images.githubusercontent.com/1443778/230463176-26808c33-3acd-4824-bb6a-46675243a98c.png)

### Horizontal Stacked Bar Chart
```kotlin
@Preview
@Composable
private fun BarChartHorizontalPreview() {
    StackedBarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        isVertical = false,
        margins = Margins.wide
    )
}
```
![hor-stacked-bar](https://user-images.githubusercontent.com/1443778/230463313-cbb25391-47fa-469d-89d7-ad4247e20b46.png)

### Stacked Area Graph
```kotlin
@Preview
@Composable
private fun StackedAreaGraphPreview() {
    StackedAreaGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 5),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}
```
![stcked-area](https://user-images.githubusercontent.com/1443778/230463623-6af1dca2-8903-4a32-8d54-979b016cfb18.png)

### Stacked 100% Area Graph
```kotlin
@Preview
@Composable
private fun Stacked100PercentAreaGraphPreview() {
    StackedAreaGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 5), is100Percent = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}
```
![stacked-area-100percent](https://user-images.githubusercontent.com/1443778/230463828-5fa71feb-f348-459f-afff-83715607e708.png)
