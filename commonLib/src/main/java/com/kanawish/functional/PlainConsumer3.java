package com.kanawish.functional;

/**
 * Functional interface for a callback that consumes multiple values at the same
 * and may throw a checked exception.
 *
 * @param <T1> the first value type
 * @param <T2> the second value type
 * @param <T3> the third value type
 */
public interface PlainConsumer3<T1, T2, T3> {

    /**
     * Consum the input parameters.
     * @param t1 the first parameter
     * @param t2 the second parameter
     * @param t3 the third parameter
     */
    void accept(T1 t1, T2 t2, T3 t3);
}
