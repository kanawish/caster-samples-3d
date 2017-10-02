package com.kanawish.mr4sg.accelerometer

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import timber.log.Timber

class AccelActivity : Activity() {
    private lateinit var mSensorManager: SensorManager

    private val mDynamicSensorCallback = object : SensorManager.DynamicSensorCallback() {
        override fun onDynamicSensorConnected(sensor: Sensor) {
            if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
                Timber.i("Accelerometer sensor connected")
                mAccelerometerListener = AccelerometerListener()
                mSensorManager.registerListener(mAccelerometerListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    private lateinit var mAccelerometerListener: AccelerometerListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAccelerometerRequest()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAccelerometerRequest()
    }

    private fun startAccelerometerRequest() {
        this.startService(Intent(this, AccelerometerService::class.java))
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorManager.registerDynamicSensorCallback(mDynamicSensorCallback)
    }

    private fun stopAccelerometerRequest() {
        this.stopService(Intent(this, AccelerometerService::class.java))
        mSensorManager.unregisterListener(mAccelerometerListener)
    }

    private inner class AccelerometerListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            Timber.i("Accelerometer event: " +
                    event.values[0] + ", " + event.values[1] + ", " + event.values[2])
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Timber.i("Accelerometer accuracy changed: " + accuracy)
        }
    }

}