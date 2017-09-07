package com.kanawish.gl.utils;

import android.os.Handler;
import android.os.Looper;

import com.kanawish.functional.PlainConsumer;

public class FpsCounter {
    private static final int FRAME_SAMPLE_SIZE = 60;

    private final Handler mainThread = new Handler(Looper.getMainLooper());
    private final PlainConsumer<Double> reporter;

    private long measureStart;
    private int frameCount = FRAME_SAMPLE_SIZE;

    public FpsCounter(PlainConsumer<Double> reporter) {
        this.reporter = reporter;
    }

    public void log() {
        if (frameCount < FRAME_SAMPLE_SIZE) {
            frameCount++;
        } else {
            report(System.nanoTime());
            frameCount = 0;
            measureStart = System.nanoTime();
        }
    }

    private void report(final long measureEnd) {
        final double ms;
        long elapsed = measureEnd - measureStart;

        if (elapsed > 0) {
            ms = (elapsed / (double) FRAME_SAMPLE_SIZE) / 1000000d;
        } else {
            ms = -1;
        }

        mainThread.post(() -> reporter.accept(ms));
    }
}