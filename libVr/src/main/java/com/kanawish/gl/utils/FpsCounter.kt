package com.kanawish.gl.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

import com.kanawish.functional.PlainConsumer

class FpsCounter(private val reporter: PlainConsumer<Double>) {

    private val mainThread = Handler(Looper.getMainLooper())

    private var measureStart: Long = 0
    private var frameCount = FRAME_SAMPLE_SIZE

    fun log() {
        if (frameCount < FRAME_SAMPLE_SIZE) {
            frameCount++
        } else {
            report(SystemClock.elapsedRealtimeNanos())
            frameCount = 1
            measureStart = SystemClock.elapsedRealtimeNanos()
        }
    }

    private fun report(measureEnd: Long) {
        val ms: Double
        val elapsed = measureEnd - measureStart

        if (elapsed > 0) {
            ms = elapsed / FRAME_SAMPLE_SIZE.toDouble() / 1000000.0
        } else {
            ms = -1.0
        }

        mainThread.post { reporter.accept(ms) }
    }

    companion object {
        private val FRAME_SAMPLE_SIZE = 60
    }
}