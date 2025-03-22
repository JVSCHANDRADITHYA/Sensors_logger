package com.example.kotlin_app

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val sensorDataMap = mutableStateMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Initialize sensor data map
        sensorList.forEach { sensor ->
            sensorDataMap[sensor.name] = "Waiting for data..."
        }

        setContent {
            SensorScreen(sensorDataMap)
        }
    }

    override fun onResume() {
        super.onResume()
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensors.forEach { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val sensorName = it.sensor.name
            val values = it.values.joinToString("\n") { value -> "%.3f".format(value) }
            sensorDataMap[sensorName] = values
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @Composable
    fun SensorScreen(sensorData: Map<String, String>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "All Sensor Data", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(10.dp))
            sensorData.forEach { (sensor, data) ->
                Text(text = "$sensor:\n$data", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
