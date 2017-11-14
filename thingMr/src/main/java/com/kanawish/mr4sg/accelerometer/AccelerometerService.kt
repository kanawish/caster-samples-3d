package com.kanawish.mr4sg.accelerometer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

import com.google.android.things.contrib.driver.mma7660fc.Mma7660FcAccelerometerDriver

import java.io.IOException

/**
 * To use this service, start it from your component (like an activity):
 * <pre>{@code
 * this.startService(Intent(this, AccelerometerService::class.java))
 * }</pre>
 */
class AccelerometerService : Service() {
    companion object {
        private val TAG = AccelerometerService::class.java.simpleName
        private val I2C_BUS = "BUS NAME"
    }

    private lateinit var mAccelerometerDriver: Mma7660FcAccelerometerDriver

    override fun onCreate() {
        setupAccelerometer()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyAccelerometer()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    private fun setupAccelerometer() {
        try {
            mAccelerometerDriver = Mma7660FcAccelerometerDriver(I2C_BUS)
            mAccelerometerDriver.register()
            Log.i(TAG, "Accelerometer driver registered")
        } catch (e: IOException) {
            Log.e(TAG, "Error initializing accelerometer driver: ", e)
        }
    }

    private fun destroyAccelerometer() {
        mAccelerometerDriver.unregister()
        try {
            mAccelerometerDriver.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing accelerometer driver: ", e)
        }
    }
}
