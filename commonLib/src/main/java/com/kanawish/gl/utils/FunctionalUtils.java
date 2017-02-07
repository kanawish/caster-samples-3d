package com.kanawish.gl.utils;

import com.kanawish.functional.PlainBiConsumer;
import com.kanawish.functional.PlainConsumer;
import com.kanawish.functional.PlainConsumer3;

import hu.akarnokd.rxjava2.functions.Consumer3;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Functional utility collection.
 */
public class FunctionalUtils {

    private FunctionalUtils() {
    }

    public static <T> PlainConsumer<T> plain(Consumer<T> consumer) {
        return (t) -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                Timber.e(e);
            }
        };
    }

    public static <T1, T2> PlainBiConsumer<T1, T2> plain(BiConsumer<T1, T2> biConsumer) {
        return (t1, t2) -> {
            try {
                biConsumer.accept(t1, t2);
            } catch (Exception e) {
                Timber.e(e);
            }
        };
    }

    public static <T1, T2, T3> PlainConsumer3<T1, T2, T3> plain(Consumer3<T1, T2, T3> consumer) {
        return (t1, t2, t3) -> {
            try {
                consumer.accept(t1, t2, t3);
            } catch (Exception e) {
                Timber.e(e);
            }
        };
    }
}
