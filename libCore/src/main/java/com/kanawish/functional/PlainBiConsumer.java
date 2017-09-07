package com.kanawish.functional;

/**
 * A functional interface (callback) that accepts two values (of possibly different types).
 * @param <T1> the first value type
 * @param <T2> the second value type
 */
public interface PlainBiConsumer<T1, T2> {

    /**
     * Performs an operation on the given values.
     * @param t1 the first value
     * @param t2 the second value
     */
    void accept(T1 t1, T2 t2) ;
}
