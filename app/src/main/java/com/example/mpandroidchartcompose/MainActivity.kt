package com.example.mpandroidchartcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.PI
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startTimestamp = 1676678577L

        // MockData : List<MedicalData>
        val mockMedicalData = (0..100).map {
            MedicalData(
                time = startTimestamp + (it * 60),
                oxygen = 2 * sin((it * 2 * PI * 0.01).toFloat()),
                glucose = 10 * sin((it * 2 * PI * 0.1).toFloat()),
                heartRate = sin(it.toFloat() * 2 * PI.toFloat() * 20),
                temperature = sin(it.toFloat() * 2 * PI.toFloat() * 15)
            )
        }

        // List<MedicalData> -> List<Float> (ListOxygenData)
        val listOxygenData = mockMedicalData.map { it.oxygen }

        // List<MedicalData> -> List<Float> (ListGlucose)
        val listGlucose = mockMedicalData.map { it.glucose }

        // List<Float> -> List<Entry> -> LineDataSet
        val oxygenDataSet = listOxygenData.createDataSetWithColor(
            datasetColor = android.graphics.Color.BLUE,
            label = "Oxygen"
        )
        val listGlucoseDataSet = listGlucose.createDataSetWithColor(
            datasetColor = android.graphics.Color.RED,
            label = "Glucose"
        )

        val lineDataOxygen = LineData(oxygenDataSet)
        val lineDataGlucose = LineData(listGlucoseDataSet)

        setContent {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(color = Color.Gray)
            ) {
                LineChartCard(lineData = lineDataOxygen)
                LineChartCard(lineData = lineDataGlucose)
            }
        }
    }
}

@Composable
fun LineChartCard(lineData: LineData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f) // (width:height) 2:1
            .padding(16.dp)
    ) {
        LineChartComponent(
            modifier = Modifier.fillMaxSize(),
            lineData = lineData
        )
    }
}

@Composable
fun LineChartComponent(modifier: Modifier = Modifier, lineData: LineData) {
    // set up data-> (x,y) -> Entry -> List<Entry> -> LineDataSet -> LineData -> LineChart(LineData)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context)
                .setupLineChart()
                .apply {
                    data = lineData
                }
        },
        update = { view ->  /*Add animation here*/ }
    )
}


// List<Float> -> List<Entry> -> LineDataSet
fun List<Float>.createDataSetWithColor(
    datasetColor: Int = android.graphics.Color.GREEN,
    label: String = "No Label"
): LineDataSet {
    // List<Float> -> List<Entry>
    val entries = this.mapIndexed { index, value ->
        Entry(index.toFloat(), value)
    }
    // List<Entry> -> LineDataSet
    return LineDataSet(entries, label).apply {
        color = datasetColor
        setDrawFilled(false)
        setDrawCircles(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }
}

fun LineChart.setupLineChart(): LineChart = this.apply {
    setTouchEnabled(true)
    isDragEnabled = true
    setScaleEnabled(true)
    setPinchZoom(true)
    description.isEnabled = false

    // set up x-axis
    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        // axisMinimum = -10f
        // axisMaximum = 10f
    }

    // set up y-axis
    axisLeft.apply {
        // axisMinimum = -5f
        // axisMaximum = 5f
        setDrawGridLines(false)
    }

    axisRight.isEnabled = false
}
