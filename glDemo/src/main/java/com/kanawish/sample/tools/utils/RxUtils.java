package com.kanawish.sample.tools.utils;

import org.apache.commons.lang3.tuple.Pair;

import io.reactivex.Observable;

/**
 */
public class RxUtils {
    /**
     * For a given window size, emits the average value.
     */
    public static Observable<Float> movingAverage(Observable<Float> o, int windowSize) {
        Pair<Integer, Float> startValue = Pair.of(0, 0f);
        return o.window(windowSize)
                .flatMap(x -> x.scan(startValue, (acc, value) -> Pair.of(acc.getLeft() + 1, acc.getRight() + value)))
                .map(pair->pair.getRight()/pair.getLeft());
    }

}
